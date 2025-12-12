package com.empmgmt.service.impl;

import com.empmgmt.dto.EmployeeKPIDTO;
import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.model.EmployeeKPI;
import com.empmgmt.model.KPI;
import com.empmgmt.repository.EmployeeKPIRepository;
import com.empmgmt.repository.KPIRepository;
import com.empmgmt.service.EmployeeKPIService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeKPIServiceImpl implements EmployeeKPIService {

    private final EmployeeKPIRepository repo;
    private final KPIRepository kpiRepo;

    @Override
    public EmployeeKPIDTO assignKPI(EmployeeKPIDTO dto) {

        EmployeeKPI e = EmployeeKPI.builder()
                .employeeId(dto.getEmployeeId())
                .kpiId(dto.getKpiId())
                .targetValue(dto.getTargetValue())
                .build();

        repo.save(e);

        dto.setId(e.getId());
        return dto;
    }

    @Override
    public List<EmployeeKPIDTO> getEmployeeKPIs(Long employeeId) {
        return repo.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public EmployeeKPIDTO submitSelfScore(Long id, Double achieved) {
        EmployeeKPI e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found"));

        e.setAchievedSelf(achieved);
        computeScore(e);

        repo.save(e);
        return mapToDTO(e);
    }

    @Override
    public EmployeeKPIDTO managerScore(Long id, Double achieved) {
        EmployeeKPI e = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found"));

        e.setAchievedManager(achieved);
        computeScore(e);

        repo.save(e);
        return mapToDTO(e);
    }

    private void computeScore(EmployeeKPI e) {
        KPI k = kpiRepo.findById(e.getKpiId())
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found"));

        double achieved = e.getAchievedManager() != null ?
                e.getAchievedManager() : e.getAchievedSelf();

        double score = (achieved / e.getTargetValue()) * k.getWeight();
        e.setFinalScore(Math.round(score * 100.0) / 100.0);
    }

    private EmployeeKPIDTO mapToDTO(EmployeeKPI e) {
        KPI k = kpiRepo.findById(e.getKpiId())
                .orElseThrow(() -> new ResourceNotFoundException("KPI not found"));

        return EmployeeKPIDTO.builder()
                .id(e.getId())
                .employeeId(e.getEmployeeId())
                .kpiId(e.getKpiId())
                .targetValue(e.getTargetValue())
                .achievedSelf(e.getAchievedSelf())
                .achievedManager(e.getAchievedManager())
                .finalScore(e.getFinalScore())
                .kpiName(k.getName())
                .kpiWeight(k.getWeight())
                .build();
    }
}
