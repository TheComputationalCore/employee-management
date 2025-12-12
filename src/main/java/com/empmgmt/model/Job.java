package com.empmgmt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String department;
    private String employmentType;   // Full-time, Part-time, Contract, Internship
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    /* ---------------------------------------------------------
       ⭐ REQUIRED FOR AI RESUME MATCHING + CAREERS PAGE
       --------------------------------------------------------- */
    @Column(columnDefinition = "TEXT")
    private String requiredSkills;   // Comma-separated list (e.g., "Java,Spring,SQL")

    /* ---------------------------------------------------------
       OPTIONAL FIELDS FOR FUTURE FEATURES (SAFE TO INCLUDE)
       --------------------------------------------------------- */
    @Column(columnDefinition = "TEXT")
    private String responsibilities;

    @Column(columnDefinition = "TEXT")
    private String qualifications;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    private Integer experienceRequired; // in years

    private Double salaryMin;
    private Double salaryMax;

    @Builder.Default
    private Boolean active = true;

    public Double getSalary() {
        if (salaryMin != null && salaryMax != null)
            return (salaryMin + salaryMax) / 2;

        if (salaryMin != null)
            return salaryMin;

        if (salaryMax != null)
            return salaryMax;

        return 0.0;
    }

}

