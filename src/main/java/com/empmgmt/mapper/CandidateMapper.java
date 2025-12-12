package com.empmgmt.mapper;

import com.empmgmt.dto.CandidateDTO;
import com.empmgmt.model.Candidate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    CandidateMapper INSTANCE = Mappers.getMapper(CandidateMapper.class);

    CandidateDTO toDTO(Candidate candidate);

    Candidate toEntity(CandidateDTO dto);
}
