package com.campusverse.repository;

import com.campusverse.model.BusinessReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessReportRepository extends JpaRepository<BusinessReport, Long> {
    boolean existsByBusinessIdAndUserId(Long businessId, Long UserId);
}
