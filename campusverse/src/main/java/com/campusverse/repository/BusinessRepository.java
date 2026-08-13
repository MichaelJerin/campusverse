package com.campusverse.repository;

import com.campusverse.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BusinessRepository extends JpaRepository<Business, Long>, JpaSpecificationExecutor<Business> {
}
