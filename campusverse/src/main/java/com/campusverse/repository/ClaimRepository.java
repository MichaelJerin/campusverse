package com.campusverse.repository;

import com.campusverse.model.Claim;
import com.campusverse.model.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByItemId(Long itemId);
    List<Claim> findByItemIdAndStatus(Long itemId, ClaimStatus claimStatus);
}
