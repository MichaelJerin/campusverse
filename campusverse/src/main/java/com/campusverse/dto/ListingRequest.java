package com.campusverse.dto;

import com.campusverse.model.ItemCondition;
import com.campusverse.model.ListingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ListingRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    private BigDecimal price;

    @NotNull(message = "Listing type is required (SELL, EXCHANGE OR EITHER)")
    private ListingType listingType;

    private ItemCondition condition;
}
