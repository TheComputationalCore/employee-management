package com.empmgmt.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "onboarding_flows")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private LocalDate createdAt;

    private LocalDate completedAt;

    private boolean completed = false;

    @OneToMany(mappedBy = "flow", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OnboardingTask> tasks = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDate.now();
    }
}
