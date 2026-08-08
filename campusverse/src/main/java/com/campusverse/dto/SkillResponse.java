package com.campusverse.dto;

import com.campusverse.model.SkillRole;
import com.campusverse.model.UserSkill;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SkillResponse {
    private Long id;
    private String interest;
    private SkillRole role;
    private Integer selfRating;
    private LocalDateTime createdAt;

    public static SkillResponse fromEntity(UserSkill skill) {
        SkillResponse res = new SkillResponse();
        res.setId(skill.getId());
        res.setInterest(skill.getInterest());
        res.setRole(skill.getRole());
        res.setSelfRating(skill.getSelfRating());
        res.setCreatedAt(skill.getCreatedAt());
        return res;
    }
}
