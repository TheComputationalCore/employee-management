package com.empmgmt.dto;

import lombok.*;

import java.util.Map;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecruitmentAnalyticsDTO {

    private Map<String, Long> funnel; // stage -> count

    private double avgTimeToHire;
    private double avgTimeToShortlist;
    private double avgTimeToInterview;
    private double avgTimeInterviewToHire;

    private Map<String, Long> jobApplicationCounts;   // jobTitle -> count

    private Map<String, Long> monthlyApplications;    // month -> count

    private List<Map<String, Object>> topCandidates;  // {name, score, job}
}
