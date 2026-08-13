package com.campusverse.service;

import com.campusverse.dto.BusinessResponse;
import com.campusverse.dto.ReviewRequest;
import com.campusverse.dto.ReviewResponse;
import com.campusverse.exception.ResourceNotFoundException;
import com.campusverse.model.Business;
import com.campusverse.model.Review;
import com.campusverse.model.User;
import com.campusverse.repository.BusinessRepository;
import com.campusverse.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BusinessRepository businessRepository;

    public ReviewResponse addReview(Long businessId, ReviewRequest request, User reviewer) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));

        if(reviewRepository.existsByBusinessIdAndUserId(businessId, reviewer.getId())) {
            throw new IllegalArgumentException("You've already reviewed this business");
        }

        Review review = Review.builder()
                .business(business)
                .user(reviewer)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        reviewRepository.save(review);
        return ReviewResponse.fromEntity(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(Long businessId) {
        return reviewRepository.findByBusinessId(businessId).stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
