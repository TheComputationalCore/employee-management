package com.empmgmt.repository;

import com.empmgmt.model.CandidateScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateScoreRepository extends JpaRepository<CandidateScore, Long> {

    Optional<CandidateScore> findByApplicationId(Long appId);
}
