package com.campusverse.dto;

import com.campusverse.model.Claim;
import com.campusverse.model.ClaimStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClaimResponse {
    private Long id;
    private Long itemId;
    private String message;
    private ClaimStatus status;
    private String claimantName;
    private Long claimantId;
    private LocalDateTime createdAt;

    public static ClaimResponse fromEntity(Claim claim) {
        ClaimResponse res = new ClaimResponse();
        res.setId(claim.getId());
        res.setItemId(claim.getItem().getId());
        res.setMessage(claim.getMessage());
        res.setStatus(claim.getStatus());
        res.setClaimantName(claim.getClaimant().getName());
        res.setClaimantId(claim.getClaimant().getId());
        res.setCreatedAt(claim.getCreatedAt());
        return res;
    }
}
