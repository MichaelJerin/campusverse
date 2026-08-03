package com.campusverse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClaimRequest {

    @NotBlank(message = "Please describe about this the item belongs to you")
    private String message;
}
