package com.campusverse.dto;

import com.campusverse.model.PurchaseRequest;
import com.campusverse.model.PurchaseRequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PurchaseRequestResponse {
    private Long id;
    private Long listingId;
    private String message;
    private PurchaseRequestStatus status;
    private String buyerName;
    private Long buyerId;
    private String contactPhoneNumber;
    private LocalDateTime createdAt;

    public static PurchaseRequestResponse fromEntity(PurchaseRequest pr, Long viewerId) {
        PurchaseRequestResponse res = new PurchaseRequestResponse();
        res.setId(pr.getId());
        res.setListingId(pr.getListing().getId());
        res.setMessage(pr.getMessage());
        res.setStatus(pr.getStatus());
        res.setBuyerName(pr.getBuyer().getName());
        res.setBuyerId(pr.getBuyer().getId());
        res.setCreatedAt(pr.getCreatedAt());

        if(pr.getStatus() == PurchaseRequestStatus.APPROVED) {
            boolean viewerIsSeller = pr.getListing().getSeller().getId().equals(viewerId);
            res.setContactPhoneNumber(viewerIsSeller ? pr.getBuyer().getPhoneNumber() : pr.getListing().getSeller().getPhoneNumber());
        }
        return res;
    }
}
