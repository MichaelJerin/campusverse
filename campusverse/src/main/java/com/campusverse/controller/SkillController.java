package com.campusverse.controller;

import com.campusverse.dto.MentorCandidateResponse;
import com.campusverse.dto.SkillRequest;
import com.campusverse.dto.SkillResponse;
import com.campusverse.model.User;
import com.campusverse.service.UserSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final UserSkillService userSkillService;

    @PostMapping
    public ResponseEntity<SkillResponse> addSkills(@Valid @RequestBody SkillRequest request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userSkillService.addSkill(request, currentUser));
    }

    @GetMapping("/me")
    public ResponseEntity<List<SkillResponse>> getMySkills(@AuthenticationPrincipal User currentUser){
        return ResponseEntity.ok(userSkillService.getMySkills(currentUser));
    }

    @GetMapping("/mentors")
    public ResponseEntity<List<MentorCandidateResponse>> findMentors(@RequestParam String interest, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userSkillService.findMentorsForInterest(interest, currentUser));
    }
}
