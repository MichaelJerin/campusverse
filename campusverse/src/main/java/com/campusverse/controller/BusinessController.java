package com.campusverse.controller;

import com.campusverse.dto.BusinessRequest;
import com.campusverse.dto.BusinessResponse;
import com.campusverse.model.User;
import com.campusverse.service.BusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping
    public ResponseEntity<BusinessResponse> create(@Valid @RequestBody BusinessRequest request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(businessService.createBusiness(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<BusinessResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag) {
        return ResponseEntity.ok(businessService.search(keyword, category, tag));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(businessService.getBusiness(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody BusinessRequest request,
                                                   @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(businessService.updateBusiness(id, request, currentUser));
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<Void> report(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        businessService.reportBusiness(id, currentUser);
        return ResponseEntity.ok().build();
    }
}