package com.campusverse.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ItemRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String category;

    @NotBlank(message = "Location is required")
    private String Location;

    @NotBlank(message = "Contact number is requried")
    private String contactNumber;

    private LocalDate eventDate;
}
