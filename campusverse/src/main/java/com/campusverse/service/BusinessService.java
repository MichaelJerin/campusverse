package com.campusverse.service;

import com.campusverse.dto.BusinessRequest;
import com.campusverse.dto.BusinessResponse;
import com.campusverse.exception.ResourceNotFoundException;
import com.campusverse.model.Business;
import com.campusverse.model.BusinessReport;
import com.campusverse.model.User;
import com.campusverse.repository.BusinessReportRepository;
import com.campusverse.repository.BusinessRepository;
import com.campusverse.repository.ReviewRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessService {

    private final BusinessRepository businessRepository;
    private final ReviewRepository reviewRepository;
    private final BusinessReportRepository businessReportRepository;

    @Value("${app.moderation.report-threshold}")
    private int reportThreshold;

    public BusinessResponse createBusiness(BusinessRequest request, User creator) {
        Business business = Business.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .address(request.getAddress())
                .tags(request.getTags())
                .mapLink(request.getMapLink())
                .createdBy(creator)
                .lastEditedBy(creator)
                .reportCount(0)
                .build();

        businessRepository.save(business);
        return toResponse(business);
    }

    @Transactional
    public BusinessResponse updateBusiness(Long id, BusinessRequest request, User editor) {
        Business business = findActiveOrThrow(id);

        business.setName(request.getName());
        business.setDescription(request.getDescription());
        business.setCategory(request.getCategory());
        business.setAddress(request.getAddress());
        business.setTags(request.getTags());
        business.setMapLink(request.getMapLink());
        business.setLastEditedBy(editor);

        return toResponse(business);
    }

    @Transactional(readOnly = true)
    public BusinessResponse getBusiness(Long id) {
        return toResponse(findActiveOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<BusinessResponse> search(String keyword, String category, String tag) {
        Specification<Business> spec = builderSearchSpec(keyword, category, tag);

        return businessRepository.findAll(spec).stream()
                .filter(b -> b.getReportCount() < reportThreshold)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void reportBusiness(Long id, User reporter) {
        Business business = findActiveOrThrow(id);

        if(businessReportRepository.existsByBusinessIdAndUserId(id, reporter.getId())) {
            throw new IllegalArgumentException("You've already reported this listing");
        }

        BusinessReport report = BusinessReport.builder()
                .business(business)
                .user(reporter)
                .build();
        businessReportRepository.save(report);

        business.setReportCount(business.getReportCount() + 1);
    }

    private Specification<Business> builderSearchSpec(String keyword, String category, String tag) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("category")), category.toLowerCase()));
            }

            if (tag != null && !tag.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("tags")), "%" + tag.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(predicates.toArray(new Predicate[0])));
        };
    }

    private Business findActiveOrThrow(Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + id));

        if(business.getReportCount() >= reportThreshold) {
            throw new ResourceNotFoundException("This listing has been removed by the community");
        }

        return business;
    }

    private BusinessResponse toResponse(Business business) {
        Double avgRating = reviewRepository.findAverageRatingByBusinessId(business.getId());
        long reviewCount = reviewRepository.countByBusinessId(business.getId());
        return BusinessResponse.fromEntity(business, avgRating, reviewCount);
    }
}
