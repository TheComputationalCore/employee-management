package com.empmgmt.repository;

import com.empmgmt.model.Candidate;
import com.empmgmt.model.CandidateStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findByStage(CandidateStage stage);

    List<Candidate> findByJobId(Long jobId);
}
