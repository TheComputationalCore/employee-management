package com.empmgmt.service.impl;

import com.empmgmt.dto.PerformanceDTO;
import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.model.PerformanceReview;
import com.empmgmt.repository.PerformanceRepository;
import com.empmgmt.service.PerformanceService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceServiceImpl implements PerformanceService {

    private final PerformanceRepository repo;

    /* ---------------------------------------------------------
       CREATE NEW PERFORMANCE CYCLE FOR EMPLOYEE
    --------------------------------------------------------- */
    @Override
    public PerformanceDTO createReview(PerformanceDTO dto) {

        boolean exists = repo.findAll().stream()
                .anyMatch(r -> r.getEmployeeId().equals(dto.getEmployeeId())
                        && r.getCycle().equalsIgnoreCase(dto.getCycle()));

        if (exists) {
            throw new IllegalArgumentException("Performance review already exists for this cycle");
        }

        PerformanceReview pr = PerformanceReview.builder()
                .employeeId(dto.getEmployeeId())
                .cycle(dto.getCycle())
                .status(PerformanceReview.ReviewStatus.PENDING)   // ✅ FIXED
                .reviewType(PerformanceReview.ReviewType.SELF)
                .build();

        repo.save(pr);
        return toDTO(pr);
    }

    /* ---------------------------------------------------------
       EMPLOYEE SELF REVIEW
    --------------------------------------------------------- */
    @Override
    public PerformanceDTO submitSelfReview(Long id, Integer rating, String comments) {
        PerformanceReview pr = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        pr.setSelfRating(rating);
        pr.setSelfComments(comments);
        pr.setStatus(PerformanceReview.ReviewStatus.SELF_SUBMITTED); // ✅ FIXED

        repo.save(pr);
        return toDTO(pr);
    }

    /* ---------------------------------------------------------
       MANAGER FINAL REVIEW
    --------------------------------------------------------- */
    @Override
    public PerformanceDTO managerReview(Long id, Integer rating, String comments) {
        PerformanceReview pr = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        pr.setManagerRating(rating);
        pr.setManagerComments(comments);
        pr.setStatus(PerformanceReview.ReviewStatus.COMPLETED); // or MANAGER_REVIEWED if you want a 2-step flow

        repo.save(pr);
        return toDTO(pr);
    }

    /* ---------------------------------------------------------
       GET REVIEWS FOR ONE EMPLOYEE
    --------------------------------------------------------- */
    @Override
    public List<PerformanceDTO> getReviewsForEmployee(Long employeeId) {
        return repo.findByEmployeeId(employeeId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /* ---------------------------------------------------------
       HR: GET ALL PERFORMANCE REVIEWS
    --------------------------------------------------------- */
    @Override
    public List<PerformanceDTO> getAllReviews() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    /* ---------------------------------------------------------
       GET ONE BY ID
    --------------------------------------------------------- */
    @Override
    public PerformanceDTO getById(Long id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    }

    /* ---------------------------------------------------------
       FINAL SCORE CALCULATION
    --------------------------------------------------------- */
    @Override
    public Integer calculateFinalScore(Integer selfRating, Integer managerRating) {
        if (selfRating == null || managerRating == null) return null;

        double result = (selfRating * 0.4) + (managerRating * 0.6);
        return (int) Math.round(result);
    }

    /* ---------------------------------------------------------
       GET BY CYCLE
    --------------------------------------------------------- */
    @Override
    public List<PerformanceDTO> getByCycle(String cycle) {
        return repo.findAll().stream()
                .filter(r -> r.getCycle().equalsIgnoreCase(cycle))
                .map(this::toDTO)
                .toList();
    }

    /* ---------------------------------------------------------
       DTO MAPPER
    --------------------------------------------------------- */
    private PerformanceDTO toDTO(PerformanceReview p) {
        return PerformanceDTO.builder()
                .id(p.getId())
                .employeeId(p.getEmployeeId())
                .cycle(p.getCycle())
                .selfRating(p.getSelfRating())
                .selfComments(p.getSelfComments())
                .managerRating(p.getManagerRating())
                .managerComments(p.getManagerComments())
                .finalScore(calculateFinalScore(p.getSelfRating(), p.getManagerRating()))
                .status(p.getStatus().name())
                .build();
    }
}
