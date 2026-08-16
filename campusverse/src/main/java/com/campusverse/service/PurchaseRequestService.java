package com.campusverse.service;

import com.campusverse.dto.PurchaseRequestCreateRequest;
import com.campusverse.dto.PurchaseRequestResponse;
import com.campusverse.exception.ResourceNotFoundException;
import com.campusverse.exception.UnauthorizedActionException;
import com.campusverse.model.*;
import com.campusverse.repository.PurchaseRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseRequestService {

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final ListingService listingService;

    public PurchaseRequestResponse submitRequest(Long listingId, PurchaseRequestCreateRequest request, User buyer) {
        Listing listing = listingService.findListingOrThow(listingId);

        if(listing.getStatus() != ListingStatus.AVAILABLE) {
            throw new IllegalArgumentException("This listing is not longer available");
        }

        if(listing.getSeller().getId().equals(buyer.getId())) {
            throw new IllegalArgumentException("You cannot request you own listing");
        }

        PurchaseRequest pr = PurchaseRequest.builder()
                .listing(listing)
                .buyer(buyer)
                .message(request.getMessage())
                .status(PurchaseRequestStatus.PENDING)
                .build();

        purchaseRequestRepository.save(pr);
        return PurchaseRequestResponse.fromEntity(pr, buyer.getId());
    }

    @Transactional(readOnly = true)
    public List<PurchaseRequestResponse> getRequestsForListing(Long listingId, User requester) {
        Listing listing = listingService.findListingOrThow(listingId);

        if(!listing.getSeller().getId().equals(requester.getId())) {
            throw new UnauthorizedActionException("Only the seller can view request on this listing");
        }

        return purchaseRequestRepository.findByListingId(listingId).stream()
                .map(pr -> PurchaseRequestResponse.fromEntity(pr, requester.getId()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PurchaseRequestResponse> getMyRequests(User buyer) {
        return purchaseRequestRepository.findByListingId(buyer.getId()).stream()
                .map(pr -> PurchaseRequestResponse.fromEntity(pr, buyer.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public PurchaseRequestResponse approveRequest(Long requestId, User requester) {
        PurchaseRequest requestToApprove = purchaseRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        Listing listing = requestToApprove.getListing();

        if(!listing.getSeller().getId().equals(requester.getId())) {
            throw new UnauthorizedActionException("Only the seller can approve requests on this listing");
        }

       if(listing.getStatus() != ListingStatus.AVAILABLE) {
           throw new IllegalArgumentException("This listing is no longer available");
       }

       requestToApprove.setStatus(PurchaseRequestStatus.APPROVED);

       List<PurchaseRequest> otherRequests = purchaseRequestRepository.findByListingIdAndStatus(listing.getId(), PurchaseRequestStatus.PENDING);
       for(PurchaseRequest other : otherRequests) {
           if(!other.getId().equals(requestToApprove.getId())) {
               other.setStatus(PurchaseRequestStatus.REJECTED);
           }
       }

       listing.setStatus(ListingStatus.RESERVED);

       return PurchaseRequestResponse.fromEntity(requestToApprove, requester.getId());
    }
}
