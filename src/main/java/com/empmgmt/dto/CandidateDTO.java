package com.empmgmt.dto;

import com.empmgmt.model.CandidateStage;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;

    private Long jobId;
    private String jobTitle;

    private CandidateStage stage;

    private String resumePath;
}
