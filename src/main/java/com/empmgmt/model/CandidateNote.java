package com.empmgmt.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "candidate_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long applicationId;

    @Column(columnDefinition = "TEXT")
    private String note;

    private String createdBy;

    private LocalDateTime createdAt;
}
