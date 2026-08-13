package com.campusverse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BusinessRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Address is required")
    private String address;

    private String tags;
    private String mapLink;
}
