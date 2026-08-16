package com.campusverse.repository;

import com.campusverse.model.PurchaseRequest;
import com.campusverse.model.PurchaseRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
    List<PurchaseRequest> findByListingId(Long listingId);
    List<PurchaseRequest> findByListingIdAndStatus(Long listingId, PurchaseRequestStatus status);
    List<PurchaseRequest> findByBuyerId(Long buyerId);
}
