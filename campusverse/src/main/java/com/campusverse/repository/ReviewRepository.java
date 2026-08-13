package com.campusverse.repository;

import com.campusverse.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBusinessId(Long businessId);
    boolean existsByBusinessIdAndUserId(Long businessId, Long userId);
    long countByBusinessId(Long businessId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.business.id = :businessId")
    Double findAverageRatingByBusinessId(@Param("businessId") Long businessId);
}
