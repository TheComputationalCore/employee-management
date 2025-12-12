package com.empmgmt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidate_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long applicationId;

    private double skillsScore;     // 0–50
    private double experienceScore; // 0–30
    private double resumeScore;     // 0–20

    private double finalScore;      // Total 0–100
}
