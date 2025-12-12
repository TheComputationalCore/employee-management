package com.empmgmt.service;

import com.empmgmt.dto.CandidateDTO;
import com.empmgmt.dto.PipelineColumnDTO;

import java.util.List;

public interface RecruitmentService {

    List<PipelineColumnDTO> getPipeline();

    void updateCandidateStage(Long candidateId, String newStage);

    CandidateDTO getCandidate(Long id);

    List<CandidateDTO> getCandidatesByJob(Long jobId);

    CandidateDTO createCandidate(CandidateDTO dto);

    void deleteCandidate(Long id);
}
