package com.empmgmt.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PipelineColumnDTO {
    private String name; // "Applied"
    private String stage; // APPLIED
    private List<CandidateDTO> candidates;
}
