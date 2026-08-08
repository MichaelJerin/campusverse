package com.campusverse.repository;

import com.campusverse.model.SkillRole;
import com.campusverse.model.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    List<UserSkill> findByUserId(Long userId);
    List<UserSkill> findByInterestAndRole(String interest, SkillRole role);
    boolean existsByUserIdAndInterestAndRole(Long userId, String interest, SkillRole role);
}
