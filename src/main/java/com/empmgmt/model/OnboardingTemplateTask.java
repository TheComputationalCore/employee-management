package com.empmgmt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "onboarding_template_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingTemplateTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private boolean required = true;

    private Integer sortOrder;


}
