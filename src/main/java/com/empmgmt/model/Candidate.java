package com.empmgmt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String phone;

    private Long jobId;

    private String jobTitle;

    @Enumerated(EnumType.STRING)
    private CandidateStage stage;  // APPLIED, SCREENING, INTERVIEW...

    private String resumePath;
}
