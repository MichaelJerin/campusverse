package com.campusverse.controller;

import com.campusverse.dto.MatchRequestCreateRequest;
import com.campusverse.dto.MatchRequestResponse;
import com.campusverse.model.User;
import com.campusverse.service.MatchRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchRequestController {

    private final MatchRequestService matchRequestService;

    @PostMapping
    public ResponseEntity<MatchRequestResponse> createRequest(@Valid @RequestBody MatchRequestCreateRequest request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(matchRequestService.createRequest(request, currentUser));
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<MatchRequestResponse>> getIncoming(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(matchRequestService.getIncomingRequests(currentUser));
    }

    @GetMapping("/outgoing")
    public ResponseEntity<List<MatchRequestResponse>> getOutgoing(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(matchRequestService.getOutgoingRequests(currentUser));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<MatchRequestResponse> accept(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(matchRequestService.respondToRequest(id, true, currentUser));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<MatchRequestResponse> reject(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(matchRequestService.respondToRequest(id, false, currentUser));
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<MatchRequestResponse> end(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(matchRequestService.endMatch(id, currentUser));
    }
}
