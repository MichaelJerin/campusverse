package com.campusverse.controller;

import com.campusverse.dto.ReviewRequest;
import com.campusverse.dto.ReviewResponse;
import com.campusverse.model.User;
import com.campusverse.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businesses/{businessId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(@PathVariable Long businessId,
                                                    @Valid @RequestBody ReviewRequest request,
                                                    @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(reviewService.addReview(businessId, request, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getReview(@PathVariable Long businessId) {
        return ResponseEntity.ok((reviewService.getReviews(businessId)));
    }
}
