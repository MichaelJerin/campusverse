package com.campusverse.service;

import com.campusverse.dto.MatchRequestCreateRequest;
import com.campusverse.dto.MatchRequestResponse;
import com.campusverse.exception.ResourceNotFoundException;
import com.campusverse.exception.UnauthorizedActionException;
import com.campusverse.model.*;
import com.campusverse.repository.MatchRequestRepository;
import com.campusverse.repository.UserRepository;
import com.campusverse.repository.UserSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchRequestService {

    private final MatchRequestRepository matchRequestRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final UserSkillService userSkillService;

    @Transactional
    public MatchRequestResponse createRequest(MatchRequestCreateRequest request, User mentee) {
        String normalizedInterest = request.getInterest().trim().toLowerCase();

        if(request.getMentorId().equals(mentee.getId())) {
            throw new IllegalArgumentException("You cannot send a mentorship request to yourself");
        }

        User mentor = userRepository.findById(request.getMentorId())
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found"));

        UserSkill mentorSkill = userSkillRepository.findByInterestAndRole(normalizedInterest, SkillRole.MENTOR).stream()
                .filter(s -> s.getUser().getId().equals(mentor.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("This user isn't listed as a mentor for that interest"));

        if(matchRequestRepository.existsByMentorIdAndMenteeIdAndInterestAndStatus(
                mentor.getId(), mentee.getId(), normalizedInterest, MatchStatus.PENDING)) {
            throw new IllegalArgumentException("You already have a pending request with is mentor for this interest");
        }

        double score = userSkillService.calculateScore(mentee.getId(), mentor.getId(), normalizedInterest, mentorSkill.getSelfRating());

        MatchRequest matchRequest = MatchRequest.builder()
                .mentor(mentor)
                .mentee(mentee)
                .interest(normalizedInterest)
                .matchScore((float)score)
                .message(request.getMessage())
                .status(MatchStatus.PENDING)
                .build();

        matchRequestRepository.save(matchRequest);
        return MatchRequestResponse.fromEntity(matchRequest, mentee.getId());
    }

    @Transactional(readOnly = true)
    public List<MatchRequestResponse> getIncomingRequests(User mentor) {
        return matchRequestRepository.findByMentorId(mentor.getId()).stream()
                .map(mr -> MatchRequestResponse.fromEntity(mr, mentor.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MatchRequestResponse> getOutgoingRequests(User mentee) {
        return matchRequestRepository.findByMenteeId(mentee.getId()).stream()
                .map(mr -> MatchRequestResponse.fromEntity(mr, mentee.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public MatchRequestResponse respondToRequest(Long matchId, boolean accept, User mentor) {
        MatchRequest matchRequest = matchRequestRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match request not found"));

        if(!matchRequest.getMentor().getId().equals(mentor.getId())) {
            throw new UnauthorizedActionException("Only the mentor can respond to this request");
        }

        if(matchRequest.getStatus() != MatchStatus.PENDING) {
            throw new IllegalArgumentException("This request has already been responded to");
        }

        matchRequest.setStatus(accept ? MatchStatus.ACCEPTED : MatchStatus.REJECTED);
        matchRequest.setRespondedAt(LocalDateTime.now());

        return MatchRequestResponse.fromEntity(matchRequest, mentor.getId());
    }

    @Transactional
    public MatchRequestResponse endMatch(Long matchId, User requester) {
        MatchRequest matchRequest = matchRequestRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("MAtch request not found"));

        boolean isParticipant = matchRequest.getMentor().getId().equals(requester.getId()) || matchRequest.getMentee().getId().equals(requester.getId());

        if(!isParticipant) {
            throw new UnauthorizedActionException("You are not part of this match");
        }

        if(matchRequest.getStatus() != MatchStatus.ACCEPTED) {
            throw new IllegalArgumentException("Only an active match can be ended");
        }

        matchRequest.setStatus(MatchStatus.ENDED);
        return MatchRequestResponse.fromEntity(matchRequest, requester.getId());
    }
}
