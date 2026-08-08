package com.campusverse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MatchRequestCreateRequest {
    @NotNull(message = "Mentor is required")
    private Long mentorId;

    @NotBlank(message = "Interest is required")
    private String interest;

    private String message;
}
