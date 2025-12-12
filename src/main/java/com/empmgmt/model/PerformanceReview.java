package com.empmgmt.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "performance_reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    /**
     * Cycle: Q1-2025, Q2-2025, H1-2025, YEAR-2025
     */
    private String cycle;

    /**
     * ENUM for review type: SELF, MANAGER, FINAL
     */
    @Enumerated(EnumType.STRING)
    private ReviewType reviewType;

    /**
     * Self-review fields
     */
    private Integer selfRating;        // Optional
    @Column(columnDefinition = "TEXT")
    private String selfComments;

    /**
     * Manager-review fields
     */
    private Integer managerRating;     // Optional
    @Column(columnDefinition = "TEXT")
    private String managerComments;

    /**
     * Auto computed final score: (self + manager) / 2
     */
    private Double finalScore;

    /**
     * Review status
     * PENDING, SELF_SUBMITTED, MANAGER_REVIEWED, COMPLETED
     */
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    /**
     * Timestamps
     */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void preCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (status == null) status = ReviewStatus.PENDING;
        if (reviewType == null) reviewType = ReviewType.SELF;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* ENUMS --------------------------------------------------- */

    public enum ReviewType {
        SELF,
        MANAGER,
        FINAL
    }

    public enum ReviewStatus {
        PENDING,
        SELF_SUBMITTED,
        MANAGER_REVIEWED,
        COMPLETED
    }
}
