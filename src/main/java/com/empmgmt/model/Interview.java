package com.empmgmt.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long applicationId;
    private Long jobId;

    private String interviewerName;

    /* ==========================================================
       🟢 REQUIRED BY SERVICES (fixes getType(), getDate(), getTime())
       ========================================================== */
    private String type;     // Online / In-Person  (alias for interviewType)
    private LocalDate date;  // interview date
    private LocalTime time;  // interview time

    /* Keep your original fields for backward compatibility */
    private String interviewType;     // original
    private String locationOrLink;

    /* ==========================================================
       🟢 REQUIRED BY RecruitmentController (getCandidateName)
       ========================================================== */
    private String candidateName;

    /* ==========================================================
       🟢 NEW — used by analytics
       ========================================================== */
    private LocalDateTime interviewScheduledAt;

    /* ========================================================== */
    @Column(columnDefinition = "TEXT")
    private String notes;
    private LocalDateTime interviewDateTime;

    /* ==========================================================
       📌 AUTO-SYNC old + new fields for safety
       ========================================================== */
    @PrePersist
    @PreUpdate
    public void syncFields() {

        // Keep old interviewDateTime in sync (if your UI uses it)
        if (date != null && time != null) {
            this.interviewDateTime = LocalDateTime.of(date, time);
        }

        if (interviewDateTime != null) {
            this.date = interviewDateTime.toLocalDate();
            this.time = interviewDateTime.toLocalTime();
        }

        // Sync type ↔ interviewType
        if (this.type == null && this.interviewType != null) {
            this.type = this.interviewType;
        }
        if (this.interviewType == null && this.type != null) {
            this.interviewType = this.type;
        }

        // Auto timestamp when scheduling
        if (interviewScheduledAt == null) {
            interviewScheduledAt = LocalDateTime.now();
        }
    }
}
