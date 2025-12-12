package com.empmgmt.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingSummaryDTO {

    private Long id;

    private String employeeName;

    private String employeeEmail;

    private int progressPercent;
}
