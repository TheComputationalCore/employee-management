package com.empmgmt.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

    // KPI Cards
    private long totalEmployees;
    private long onboardingActive;
    private long pendingLeaves;
    private long openJobs;

    // Recruitment Pipeline
    private long appliedCount;
    private long screeningCount;
    private long interviewCount;
    private long offerCount;

    // Employee Growth Chart
    private List<String> months;
    private List<Long> employeeCounts;

    // Leave Breakdown
    private long leavesApproved;
    private long leavesPending;
    private long leavesRejected;

    // Onboarding Summary
    private List<?> onboardingList;
}
