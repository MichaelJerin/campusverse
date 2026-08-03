package com.campusverse.service;

import com.campusverse.dto.ClaimRequest;
import com.campusverse.dto.ClaimResponse;
import com.campusverse.exception.ResourceNotFoundException;
import com.campusverse.exception.UnauthorizedActionException;
import com.campusverse.model.*;
import com.campusverse.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemService itemService;

    @Transactional
    public ClaimResponse submitClaims(Long itemId, ClaimRequest request, User claimant) {
        Item item = itemService.findItemOrThrow(itemId);

        if(item.getStatus() == ItemStatus.CLOSED) {
            throw new IllegalArgumentException("This item is already closed and no longer accepting claims");
        }

        if(item.getUser().getId().equals(claimant.getId())) {
            throw new IllegalArgumentException("You cannot claim an item you reported as found");
        }

        Claim claim = Claim.builder()
                .item(item)
                .claimant(claimant)
                .message(request.getMessage())
                .status(ClaimStatus.PENDING)
                .build();

        claimRepository.save(claim);
        return ClaimResponse.fromEntity(claim);
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimForItem(Long itemId, User requester) {
        Item item = itemService.findItemOrThrow(itemId);

        if(!item.getUser().getId().equals(requester.getId())) {
            throw new UnauthorizedActionException("Only the finder can view claims on this item");
        }

        return claimRepository.findByItemId(itemId).stream()
                .map(ClaimResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClaimResponse approveClaim(Long claimId, User requester) {
        Claim claimToApprove = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + claimId));

        Item item = claimToApprove.getItem();

        if (!item.getUser().getId().equals(requester.getId())) {
            throw new UnauthorizedActionException("Only the finder can approve claims on this item");
        }

        if(item.getStatus() == ItemStatus.CLOSED) {
            throw new IllegalArgumentException("This itemis already closed");
        }

        // approve the chosen clai
        claimToApprove.setStatus(ClaimStatus.APPROVED);
        claimRepository.save(claimToApprove);

        // Auto reject every other pending on this item
        List<Claim> otherClaims = claimRepository.findByItemIdAndStatus(item.getId(), ClaimStatus.PENDING);
        for(Claim other : otherClaims) {
            if(!other.getId().equals(claimToApprove.getId())) {
                other.setStatus(ClaimStatus.REJECTED);
                claimRepository.save(other);
            }
        }

        item.setStatus(ItemStatus.CLOSED);

        return ClaimResponse.fromEntity(claimToApprove);
    }
}
