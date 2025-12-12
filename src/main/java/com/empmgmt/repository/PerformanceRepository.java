package com.empmgmt.repository;

import com.empmgmt.model.PerformanceReview;
import com.empmgmt.model.PerformanceReview.ReviewStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<PerformanceReview, Long> {

    // All reviews for an employee
    List<PerformanceReview> findByEmployeeId(Long employeeId);

    // Reviews for a specific cycle (Q1-2025, YEAR-2025 etc.)
    List<PerformanceReview> findByCycle(String cycle);

    // Unique employee-cycle check
    Optional<PerformanceReview> findByEmployeeIdAndCycle(Long employeeId, String cycle);

    // Manager dashboard – pending manager reviews
    List<PerformanceReview> findByStatus(ReviewStatus status);

    // Employee dashboard — reviews awaiting employee submission
    List<PerformanceReview> findByEmployeeIdAndStatus(Long employeeId, ReviewStatus status);

    // Analytics — average score by cycle
    @Query("SELECT AVG(r.finalScore) FROM PerformanceReview r WHERE r.cycle = :cycle")
    Double averageScoreByCycle(String cycle);

    // Analytics — ratings distribution count (1–5)
    @Query("""
        SELECT r.managerRating, COUNT(r)
        FROM PerformanceReview r
        WHERE r.cycle = :cycle AND r.managerRating IS NOT NULL
        GROUP BY r.managerRating
    """)
    List<Object[]> ratingDistribution(String cycle);
}
