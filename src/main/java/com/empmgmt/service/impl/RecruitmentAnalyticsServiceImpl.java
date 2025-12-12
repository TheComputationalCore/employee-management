package com.empmgmt.service.impl;

import com.empmgmt.dto.RecruitmentAnalyticsDTO;
import com.empmgmt.model.Application;
import com.empmgmt.repository.ApplicationRepository;
import com.empmgmt.repository.CandidateScoreRepository;
import com.empmgmt.repository.JobRepository;
import com.empmgmt.service.RecruitmentAnalyticsService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentAnalyticsServiceImpl implements RecruitmentAnalyticsService {

    private final ApplicationRepository appRepo;
    private final JobRepository jobRepo;
    private final CandidateScoreRepository scoreRepo;

    @Override
    public RecruitmentAnalyticsDTO getAnalytics() {

        List<Application> apps = appRepo.findAll();

        /* -----------------------------------------------------------
         * 1. Funnel
         * ----------------------------------------------------------- */
        Map<String, Long> funnel = apps.stream()
                .collect(Collectors.groupingBy(
                        Application::getStatus,
                        Collectors.counting()
                ));

        /* -----------------------------------------------------------
         * 2. Job Application Count
         * ----------------------------------------------------------- */
        Map<String, Long> jobCounts = apps.stream()
                .collect(Collectors.groupingBy(
                        a -> jobRepo.findById(a.getJobId())
                                .map(j -> j.getTitle()).orElse("Unknown"),
                        Collectors.counting()
                ));

        /* -----------------------------------------------------------
         * 3. Monthly Applications
         * ----------------------------------------------------------- */
        Map<String, Long> monthly = apps.stream()
                .filter(a -> a.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getCreatedAt()
                                .getMonth()
                                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        Collectors.counting()
                ));

        /* -----------------------------------------------------------
         * 4. Duration Metrics
         * ----------------------------------------------------------- */
        double avgShortlist = calculateAvgDuration(
                apps,
                Application::getCreatedAt,
                a -> toDate(a.getShortlistedAt())
        );

        double avgInterview = calculateAvgDuration(
                apps,
                a -> toDate(a.getShortlistedAt()),
                a -> toDate(a.getInterviewScheduledAt())
        );

        double avgInterviewToHire = calculateAvgDuration(
                apps,
                a -> toDate(a.getInterviewScheduledAt()),
                a -> toDate(a.getHiredAt())
        );

        double avgTimeToHire = calculateAvgDuration(
                apps,
                Application::getCreatedAt,
                Application::getStatusUpdatedAt
        );

        /* -----------------------------------------------------------
         * 5. Top Candidates
         * ----------------------------------------------------------- */
        var scores = scoreRepo.findAll().stream()
                .sorted(Comparator.comparingDouble(s -> -s.getFinalScore()))
                .limit(5)
                .map(s -> {
                    var app = appRepo.findById(s.getApplicationId()).orElse(null);
                    if (app == null) return null;

                    Map<String, Object> info = new HashMap<>();
                    info.put("name", app.getFullName());
                    info.put("job", jobRepo.findById(app.getJobId())
                            .map(j -> j.getTitle()).orElse("N/A"));
                    info.put("score", s.getFinalScore());
                    return info;
                })
                .filter(Objects::nonNull)
                .toList();

        return RecruitmentAnalyticsDTO.builder()
                .funnel(funnel)
                .jobApplicationCounts(jobCounts)
                .monthlyApplications(monthly)
                .avgTimeToShortlist(avgShortlist)
                .avgTimeToInterview(avgInterview)
                .avgTimeInterviewToHire(avgInterviewToHire)
                .avgTimeToHire(avgTimeToHire)
                .topCandidates(scores)
                .build();
    }

    /* -----------------------------------------------------------
     * LocalDateTime → LocalDate (null safe)
     * ----------------------------------------------------------- */
    private LocalDate toDate(java.time.LocalDateTime dt) {
        return dt == null ? null : dt.toLocalDate();
    }

    /* -----------------------------------------------------------
     * Average Duration Calculator
     * ----------------------------------------------------------- */
    private double calculateAvgDuration(
            List<Application> apps,
            java.util.function.Function<Application, LocalDate> startExtractor,
            java.util.function.Function<Application, LocalDate> endExtractor
    ) {
        List<Long> durations = apps.stream()
                .map(a -> {
                    LocalDate start = startExtractor.apply(a);
                    LocalDate end = endExtractor.apply(a);
                    if (start == null || end == null) return null;
                    return Duration.between(start.atStartOfDay(), end.atStartOfDay()).toDays();
                })
                .filter(Objects::nonNull)
                .toList();

        if (durations.isEmpty()) return 0;
        return durations.stream().mapToLong(d -> d).average().orElse(0);
    }
}
