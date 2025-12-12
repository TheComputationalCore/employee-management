package com.empmgmt.mapper;

import com.empmgmt.dto.CandidateDTO;
import com.empmgmt.model.Candidate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-10T18:53:59+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class CandidateMapperImpl implements CandidateMapper {

    @Override
    public CandidateDTO toDTO(Candidate candidate) {
        if ( candidate == null ) {
            return null;
        }

        CandidateDTO.CandidateDTOBuilder candidateDTO = CandidateDTO.builder();

        candidateDTO.id( candidate.getId() );
        candidateDTO.name( candidate.getName() );
        candidateDTO.email( candidate.getEmail() );
        candidateDTO.phone( candidate.getPhone() );
        candidateDTO.jobId( candidate.getJobId() );
        candidateDTO.jobTitle( candidate.getJobTitle() );
        candidateDTO.stage( candidate.getStage() );
        candidateDTO.resumePath( candidate.getResumePath() );

        return candidateDTO.build();
    }

    @Override
    public Candidate toEntity(CandidateDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Candidate.CandidateBuilder candidate = Candidate.builder();

        candidate.id( dto.getId() );
        candidate.name( dto.getName() );
        candidate.email( dto.getEmail() );
        candidate.phone( dto.getPhone() );
        candidate.jobId( dto.getJobId() );
        candidate.jobTitle( dto.getJobTitle() );
        candidate.stage( dto.getStage() );
        candidate.resumePath( dto.getResumePath() );

        return candidate.build();
    }
}
