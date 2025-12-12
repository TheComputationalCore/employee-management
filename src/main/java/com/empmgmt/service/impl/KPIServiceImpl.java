package com.empmgmt.service.impl;

import com.empmgmt.dto.KPIDTO;
import com.empmgmt.model.KPI;
import com.empmgmt.repository.KPIRepository;
import com.empmgmt.service.KPIService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KPIServiceImpl implements KPIService {

    private final KPIRepository repo;

    @Override
    public KPIDTO create(KPIDTO dto) {
        KPI kpi = KPI.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .weight(dto.getWeight())
                .build();

        repo.save(kpi);

        dto.setId(kpi.getId());
        return dto;
    }

    @Override
    public List<KPIDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(k -> KPIDTO.builder()
                        .id(k.getId())
                        .name(k.getName())
                        .description(k.getDescription())
                        .weight(k.getWeight())
                        .build())
                .toList();
    }
}
