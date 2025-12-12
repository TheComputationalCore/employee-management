package com.empmgmt.repository;

import com.empmgmt.model.CandidateNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateNoteRepository extends JpaRepository<CandidateNote, Long> {

    List<CandidateNote> findByApplicationIdOrderByCreatedAtDesc(Long appId);
}
