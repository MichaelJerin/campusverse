package com.campusverse.repository;

import com.campusverse.model.MatchRequest;
import com.campusverse.model.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {
    List<MatchRequest> findByMentorId(Long mentorId);
    List<MatchRequest> findByMenteeId(Long menteeId);
    boolean existsByMentorIdAndMenteeIdAndInterestAndStatus(Long mentorId, Long menteeId, String interest, MatchStatus status);
}
