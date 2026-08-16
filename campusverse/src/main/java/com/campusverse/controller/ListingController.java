package com.campusverse.controller;

import com.campusverse.dto.ListingRequest;
import com.campusverse.dto.ListingResponse;
import com.campusverse.model.ListingStatus;
import com.campusverse.model.User;
import com.campusverse.service.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    @PostMapping
    public ResponseEntity<ListingResponse> create(@Valid @RequestBody ListingRequest request, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(listingService.createListing(request, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<ListingResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false)ListingStatus status) {
        return ResponseEntity.ok(listingService.search(keyword, category, minPrice, maxPrice, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(listingService.getListing(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListingResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody ListingRequest request,
                                                  @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(listingService.updateListing(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        listingService.deleteListing(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/mark-sold")
    public ResponseEntity<ListingResponse> markSold(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(listingService.markSold(id, currentUser));
    }


}
