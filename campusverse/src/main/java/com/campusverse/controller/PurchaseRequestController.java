package com.campusverse.controller;

import com.campusverse.dto.PurchaseRequestCreateRequest;
import com.campusverse.dto.PurchaseRequestResponse;
import com.campusverse.model.User;
import com.campusverse.service.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PurchaseRequestController {

    private final PurchaseRequestService purchaseRequestService;

    @PostMapping("/api/listings/{listingId}/requests")
    public ResponseEntity<PurchaseRequestResponse> submit(@PathVariable Long listingId, @RequestBody PurchaseRequestCreateRequest request,
                                                          @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(purchaseRequestService.submitRequest(listingId, request, currentUser));
    }

    @GetMapping("/api/listings/{listingId}/requests")
    public ResponseEntity<List<PurchaseRequestResponse>> getForListing(@PathVariable Long listingId, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(purchaseRequestService.getRequestsForListing(listingId, currentUser));
    }

    @GetMapping("/api/requests/mine")
    public ResponseEntity<List<PurchaseRequestResponse>> getMine(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(purchaseRequestService.getMyRequests(currentUser));
    }

    @PutMapping("/api/requests/{id}/approve")
    public ResponseEntity<PurchaseRequestResponse> approve(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(purchaseRequestService.approveRequest(id, currentUser));
    }

}
