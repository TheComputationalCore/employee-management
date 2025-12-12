package com.empmgmt.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeKPIDTO {

    private Long id;
    private Long employeeId;
    private Long kpiId;

    private Double targetValue;
    private Double achievedSelf;
    private Double achievedManager;

    private Double finalScore;

    // Display data
    private String kpiName;
    private Double kpiWeight;
}
