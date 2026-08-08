package com.campusverse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MentorCandidateResponse {
    private Long mentorId;
    private String mentorName;
    private String interest;
    private Integer selfRating;
    private int sharedInterestCount;
    private double matchScore;
}
