package com.empmgmt.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;

    private String fullName;
    private String email;
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String skills;

    private String resumePath; // Stored CV path

    private String status; // Applied, Shortlisted, Interviewing, Hired, Rejected


    /* ===============================================================
       ⭐ REQUIRED FOR EMPLOYEE CREATION
       =============================================================== */
    private String department;        // comes from Job / resume matching
    private String appliedPosition;   // job title applied for


    /* ===============================================================
       ⭐ REQUIRED FOR RECRUITMENT PIPELINE & ANALYTICS
       =============================================================== */
    private LocalDateTime shortlistedAt;
    private LocalDateTime interviewScheduledAt;
    private LocalDateTime hiredAt;
    private LocalDateTime rejectedAt;


    /* ===============================================================
       ⭐ AI ANALYSIS FIELDS
       =============================================================== */

    @Column(columnDefinition = "TEXT")
    private String parsedSkills;       // Extracted skills

    private Integer experienceYears;   // AI extracted experience

    private String education;          // AI extracted education level

    private Integer aiScore;           // AI match score (0–100)

    @Column(columnDefinition = "TEXT")
    private String aiSummary;          // AI summary of resume

    @Column(columnDefinition = "TEXT")
    private String missingSkills;      // Required - matched skills gap


    /* ===============================================================
       ⭐ TIMELINE METADATA (Required for analytics)
       =============================================================== */
    private LocalDate createdAt;
    private LocalDate statusUpdatedAt;


    /* ===============================================================
       ⭐ LIFECYCLE HOOKS
       =============================================================== */
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDate.now();
        this.statusUpdatedAt = LocalDate.now();
        if (this.status == null) this.status = "Applied";
    }

    @PreUpdate
    public void onUpdate() {
        this.statusUpdatedAt = LocalDate.now();

        // Auto-set milestone timestamps based on status transitions
        switch (status) {
            case "Shortlisted" -> {
                if (shortlistedAt == null) shortlistedAt = LocalDateTime.now();
            }
            case "Interviewing" -> {
                if (interviewScheduledAt == null) interviewScheduledAt = LocalDateTime.now();
            }
            case "Hired" -> {
                if (hiredAt == null) hiredAt = LocalDateTime.now();
            }
            case "Rejected" -> {
                if (rejectedAt == null) rejectedAt = LocalDateTime.now();
            }
        }
    }
}
