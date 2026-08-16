package com.campusverse.dto;

import com.campusverse.model.ItemCondition;
import com.campusverse.model.Listing;
import com.campusverse.model.ListingStatus;
import com.campusverse.model.ListingType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ListingResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private BigDecimal price;
    private ListingType listingType;
    private ItemCondition condition;
    private String imageUrl;
    private ListingStatus status;
    private String sellerName;
    private Long sellerId;
    private LocalDateTime createdAt;

    public static ListingResponse fromEntity(Listing listing) {
        ListingResponse res = new ListingResponse();
        res.setId(listing.getId());
        res.setTitle(listing.getTitle());
        res.setDescription(listing.getDescription());
        res.setCategory(listing.getCategory());
        res.setPrice(listing.getPrice());
        res.setListingType(listing.getListingType());
        res.setCondition(listing.getCondition());
        res.setImageUrl(listing.getImageUrl());
        res.setStatus(listing.getStatus());
        res.setSellerName(listing.getSeller().getName());
        res.setSellerId(listing.getSeller().getId());
        res.setCreatedAt(listing.getCreatedAt());
        return res;
    }
}
