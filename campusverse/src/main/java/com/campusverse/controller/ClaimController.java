package com.campusverse.controller;

import com.campusverse.dto.ClaimRequest;
import com.campusverse.dto.ClaimResponse;
import com.campusverse.model.User;
import com.campusverse.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @PostMapping("/api/items/{itemId}/claims")
    public ResponseEntity<ClaimResponse> submitClaims(@PathVariable Long itemId,
                                                      @Valid @RequestBody ClaimRequest request,
                                                      @AuthenticationPrincipal User currentUSer) {
        return ResponseEntity.ok(claimService.submitClaims(itemId, request, currentUSer));
    }

    @GetMapping("/api/items/{itemId}/claims")
    public ResponseEntity<List<ClaimResponse>> getClaims(@PathVariable Long itemId,
                                                         @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(claimService.getClaimForItem(itemId, currentUser));
    }

    @PostMapping("/api/claims/{claimId}/approve")
    public ResponseEntity<ClaimResponse> approveClaims(@PathVariable Long claimId,
                                                       @AuthenticationPrincipal User currentUSer) {
        return ResponseEntity.ok(claimService.approveClaim(claimId, currentUSer));
    }

}
