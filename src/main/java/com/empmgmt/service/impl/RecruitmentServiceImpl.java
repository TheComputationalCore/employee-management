package com.empmgmt.service.impl;

import com.empmgmt.dto.CandidateDTO;
import com.empmgmt.dto.PipelineColumnDTO;
import com.empmgmt.mapper.CandidateMapper;
import com.empmgmt.model.Candidate;
import com.empmgmt.model.CandidateStage;
import com.empmgmt.repository.CandidateRepository;
import com.empmgmt.service.RecruitmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecruitmentServiceImpl implements RecruitmentService {

    private final CandidateRepository candidateRepo;
    private final CandidateMapper mapper;

    @Override
    public List<PipelineColumnDTO> getPipeline() {

        List<Candidate> all = candidateRepo.findAll();
        List<PipelineColumnDTO> pipeline = new ArrayList<>();

        for (CandidateStage stage : CandidateStage.values()) {

            List<CandidateDTO> candidates = all.stream()
                    .filter(c -> c.getStage() == stage)
                    .map(mapper::toDTO)
                    .toList();

            pipeline.add(
                    PipelineColumnDTO.builder()
                            .name(format(stage))
                            .stage(stage.name())
                            .candidates(candidates)
                            .build()
            );
        }

        return pipeline;
    }

    private String format(CandidateStage s) {
        return switch (s) {
            case APPLIED -> "Applied";
            case SCREENING -> "Screening";
            case INTERVIEW -> "Interview";
            case OFFERED -> "Offered";
            case HIRED -> "Hired";
            case REJECTED -> "Rejected";
        };
    }

    @Override
    public void updateCandidateStage(Long id, String newStage) {
        Candidate c = candidateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        c.setStage(CandidateStage.valueOf(newStage));
        candidateRepo.save(c);
    }

    @Override
    public CandidateDTO getCandidate(Long id) {
        return candidateRepo.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
    }

    @Override
    public List<CandidateDTO> getCandidatesByJob(Long jobId) {
        return candidateRepo.findByJobId(jobId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public CandidateDTO createCandidate(CandidateDTO dto) {
        Candidate entity = mapper.toEntity(dto);
        return mapper.toDTO(candidateRepo.save(entity));
    }

    @Override
    public void deleteCandidate(Long id) {
        candidateRepo.deleteById(id);
    }
}
