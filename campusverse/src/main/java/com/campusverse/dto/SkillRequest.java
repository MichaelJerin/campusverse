package com.campusverse.dto;

import com.campusverse.model.SkillRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SkillRequest {

    @NotBlank(message = "Interest is required")
    private String interest;

    @NotNull(message = "Role is required (Mentor or Learner)")
    private SkillRole role;

    private Integer selfRating;

}
