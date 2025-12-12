package com.empmgmt.service;

import com.empmgmt.dto.PerformanceDTO;

import java.util.List;

public interface PerformanceService {

    /* ---------------------------------------------------------
       CREATE PERFORMANCE CYCLE REVIEW
       (HR creates review assignment for employee)
       --------------------------------------------------------- */
    PerformanceDTO createReview(PerformanceDTO dto);


    /* ---------------------------------------------------------
       EMPLOYEE SELF-REVIEW SUBMISSION
       --------------------------------------------------------- */
    PerformanceDTO submitSelfReview(Long reviewId, Integer rating, String comments);


    /* ---------------------------------------------------------
       MANAGER REVIEW (final assessment)
       --------------------------------------------------------- */
    PerformanceDTO managerReview(Long reviewId, Integer rating, String comments);


    /* ---------------------------------------------------------
       RETRIEVE EMPLOYEE’S ENTIRE PERFORMANCE HISTORY
       --------------------------------------------------------- */
    List<PerformanceDTO> getReviewsForEmployee(Long employeeId);


    /* ---------------------------------------------------------
       ADMIN / HR VIEW ALL REVIEWS
       --------------------------------------------------------- */
    List<PerformanceDTO> getAllReviews();


    /* ---------------------------------------------------------
       GET ONE REVIEW BY ID
       (used by self-review page + manager review page)
       --------------------------------------------------------- */
    PerformanceDTO getById(Long id);


    /* ---------------------------------------------------------
       CALCULATE FINAL SCORE
       formula: (selfRating * 0.4 + managerRating * 0.6)
       --------------------------------------------------------- */
    Integer calculateFinalScore(Integer selfRating, Integer managerRating);


    /* ---------------------------------------------------------
       GET ALL REVIEWS FOR A SPECIFIC CYCLE
       (Used in Analytics UI)
       --------------------------------------------------------- */
    List<PerformanceDTO> getByCycle(String cycle);
}
