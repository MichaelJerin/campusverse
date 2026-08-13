package com.campusverse.dto;

import com.campusverse.model.Business;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BusinessResponse {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String address;
    private String tags;
    private String imageUrl;
    private String mapLink;
    private String createdByName;
    private String lastEditedByName;
    private Integer reportCount;
    private Double averageRating;
    private Long reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BusinessResponse fromEntity(Business business, Double averageRating, Long reviewCount) {
        BusinessResponse res = new BusinessResponse();
        res.setId(business.getId());
        res.setName(business.getName());
        res.setDescription(business.getDescription());
        res.setCategory(business.getCategory());
        res.setAddress(business.getAddress());
        res.setTags(business.getTags());
        res.setImageUrl(business.getImageUrl());
        res.setMapLink(business.getMapLink());
        res.setCreatedByName(business.getCreatedBy().getName());
        res.setLastEditedByName(business.getLastEditedBy() != null ? business.getLastEditedBy().getName() : null);
        res.setReportCount(business.getReportCount());
        res.setAverageRating(averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : null);
        res.setReviewCount(reviewCount);
        res.setCreatedAt(business.getCreatedAt());
        res.setUpdatedAt(business.getUpdatedAt());
        return res;
    }
}
