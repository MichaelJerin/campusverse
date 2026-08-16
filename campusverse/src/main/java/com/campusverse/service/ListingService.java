package com.campusverse.service;

import com.campusverse.dto.ListingRequest;
import com.campusverse.dto.ListingResponse;
import com.campusverse.exception.ResourceNotFoundException;
import com.campusverse.exception.UnauthorizedActionException;
import com.campusverse.model.Listing;
import com.campusverse.model.ListingStatus;
import com.campusverse.model.User;
import com.campusverse.repository.ListingRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;

    public ListingResponse createListing(ListingRequest request, User seller) {
        Listing listing = Listing.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .listingType(request.getListingType())
                .condition(request.getCondition())
                .seller(seller)
                .status(ListingStatus.AVAILABLE)
                .build();

        listingRepository.save(listing);
        return ListingResponse.fromEntity(listing);
    }

    @Transactional(readOnly = true)
    public ListingResponse getListing(Long id) {
        return ListingResponse.fromEntity(findListingOrThow(id));
    }

    @Transactional(readOnly = true)
    public List<ListingResponse> search(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice, ListingStatus status) {
        Specification<Listing> spec = buildSearchSpec(keyword, category, minPrice, maxPrice, status);

        return listingRepository.findAll(spec).stream()
                .map(ListingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ListingResponse updateListing(Long id, ListingRequest request, User editor) {
        Listing listing = findListingOrThow(id);

        if(!listing.getSeller().getId().equals(editor.getId())) {
            throw new UnauthorizedActionException("Only the seller can edit this listing.");
        }

        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setCategory(request.getCategory());
        listing.setPrice(request.getPrice());
        listing.setListingType(request.getListingType());
        listing.setCondition(request.getCondition());

        return ListingResponse.fromEntity(listing);
    }

    public void deleteListing(Long id, User requester) {
        Listing listing = findListingOrThow(id);

        if(!listing.getSeller().getId().equals(requester.getId())) {
            throw new UnauthorizedActionException("Only the seller can remove this listing");
        }

        listingRepository.delete(listing);
    }

    @Transactional
    public ListingResponse markSold(Long id, User requester) {
        Listing listing = findListingOrThow(id);

        if(!listing.getSeller().getId().equals(requester.getId())) {
            throw new UnauthorizedActionException("Only the seller can mark this listing as sold");
        }

        listing.setStatus(ListingStatus.SOLD);
        return ListingResponse.fromEntity(listing);
    }

    private Specification<Listing> buildSearchSpec(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice, ListingStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            if(category != null && !category.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("category")), category.toLowerCase()));
            }

            if(minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if(maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if(status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            } else {
                predicates.add(cb.notEqual(root.get("status"), ListingStatus.SOLD));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    protected Listing findListingOrThow(Long id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found with id: " + id));
    }
}
