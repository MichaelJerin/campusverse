package com.campusverse.service;

import com.campusverse.dto.MentorCandidateResponse;
import com.campusverse.dto.SkillRequest;
import com.campusverse.dto.SkillResponse;
import com.campusverse.model.SkillRole;
import com.campusverse.model.User;
import com.campusverse.model.UserSkill;
import com.campusverse.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSkillService {

    private final UserSkillRepository userSkillRepository;

    public SkillResponse addSkill(SkillRequest request, User user) {
        String normalizedInterest = request.getInterest().trim().toLowerCase();

        if(request.getRole() == SkillRole.MENTOR) {
            if(request.getSelfRating() == null || request.getSelfRating() < 1 || request.getSelfRating() > 5) {
                throw new IllegalArgumentException("Mentor must provide a self-rating between 1 and 5");
            }
        }

        if(userSkillRepository.existsByUserIdAndInterestAndRole(user.getId(), normalizedInterest, request.getRole())) {
            throw new IllegalArgumentException("You've already add this interest as a " + request.getRole());
        }

        UserSkill skill = UserSkill.builder()
                .user(user)
                .interest(normalizedInterest)
                .role(request.getRole())
                .selfRating(request.getRole() == SkillRole.MENTOR ? request.getSelfRating() : null)
                .build();

        userSkillRepository.save(skill);
        return SkillResponse.fromEntity(skill);
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> getMySkills(User user) {
        return userSkillRepository.findByUserId(user.getId()).stream()
                .map(SkillResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MentorCandidateResponse> findMentorsForInterest(String interest, User requester) {
        String normalizedInterest = interest.trim().toLowerCase();

        List<UserSkill> mentorSkills = userSkillRepository.findByInterestAndRole(normalizedInterest, SkillRole.MENTOR);

        Set<String> requestInterest = userSkillRepository.findByUserId(requester.getId()).stream()
                .map(UserSkill::getInterest)
                .filter(i -> !i.equals(normalizedInterest))
                .collect(Collectors.toSet());

        return mentorSkills.stream()
                .filter(skill -> !skill.getUser().getId().equals(requester.getId()))
                .map(skill -> {
                    Set<String> mentorInterest = userSkillRepository.findByUserId(skill.getUser().getId()).stream()
                            .map(UserSkill::getInterest)
                            .filter(i -> !i.equals(normalizedInterest))
                            .collect(Collectors.toSet());

                    mentorInterest.retainAll(requestInterest);
                    int sharedCount = mentorInterest.size();

                    double score = (skill.getSelfRating() != null ? skill.getSelfRating() : 0) * 2.0 + sharedCount;

                    return new MentorCandidateResponse(
                            skill.getUser().getId(),
                            skill.getUser().getName(),
                            normalizedInterest,
                            skill.getSelfRating(),
                            sharedCount,
                            score
                    );
                })
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public double calculateScore(Long menteeId, Long mentorId, String interest, Integer mentorSelfRating) {
        String normalizedInterest = interest.trim().toLowerCase();

        Set<String> menteeInterests = userSkillRepository.findByUserId(menteeId).stream()
                .map(UserSkill::getInterest)
                .filter(i -> !i.equals(normalizedInterest))
                .collect(Collectors.toSet());

        Set<String> mentorInterests = userSkillRepository.findByUserId(mentorId).stream()
                .map(UserSkill::getInterest)
                .filter(i -> !i.equals(normalizedInterest))
                .collect(Collectors.toSet());

        mentorInterests.retainAll(menteeInterests);
        int sharedCount = mentorInterests.size();

        return (mentorSelfRating != null ? mentorSelfRating : 0) * 2.0 + sharedCount;
    }
}
