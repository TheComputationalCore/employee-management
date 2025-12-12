package com.empmgmt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee_kpis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeKPI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;
    private Long kpiId;

    private Double targetValue;     // Target (e.g., 50 sales)
    private Double achievedSelf;    // Employee self-evaluation
    private Double achievedManager; // Manager evaluation

    private Double finalScore;      // Computed weighted score
}
