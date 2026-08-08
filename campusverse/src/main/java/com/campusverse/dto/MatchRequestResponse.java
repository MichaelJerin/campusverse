package com.campusverse.dto;

import com.campusverse.model.MatchRequest;
import com.campusverse.model.MatchStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchRequestResponse {
    private Long id;
    private Long mentorId;
    private String mentorName;
    private Long menteeId;
    private String menteeName;
    private String interest;
    private Float matchScore;
    private String message;
    private MatchStatus status;
    private String contactPhoneNumber; //only populated once accepted
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    public static MatchRequestResponse fromEntity(MatchRequest mr, Long viewerId) {
        MatchRequestResponse res = new MatchRequestResponse();
        res.setId(mr.getId());
        res.setMentorId(mr.getMentor().getId());
        res.setMentorName(mr.getMentor().getName());
        res.setMenteeId(mr.getMentee().getId());
        res.setMenteeName(mr.getMentee().getName());
        res.setInterest(mr.getInterest());
        res.setMatchScore(mr.getMatchScore());
        res.setMessage(mr.getMessage());
        res.setStatus(mr.getStatus());
        res.setCreatedAt(mr.getCreatedAt());
        res.setRespondedAt(mr.getRespondedAt());

        if(mr.getStatus() == MatchStatus.ACCEPTED) {
            boolean viewIsMentor = mr.getMentor().getId().equals(viewerId);
            res.setContactPhoneNumber(viewIsMentor
            ? mr.getMentee().getPhoneNumber()
                    : mr.getMentor().getPhoneNumber());
        }
        return res;
    }
}
