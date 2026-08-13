package com.campusverse.dto;

import com.campusverse.model.Review;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private Integer rating;
    private String comment;
    private String reviewerName;
    private LocalDateTime createdAt;

    public static ReviewResponse fromEntity(Review review) {
        ReviewResponse res = new ReviewResponse();
        res.setId(review.getId());
        res.setRating(review.getRating());
        res.setComment(review.getComment());
        res.setReviewerName(review.getUser().getName());
        res.setCreatedAt(review.getCreatedAt());
        return res;
    }
}
