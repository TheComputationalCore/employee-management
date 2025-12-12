package com.empmgmt.service.impl;

import com.empmgmt.dto.DashboardDTO;
import com.empmgmt.dto.OnboardingSummaryDTO;
import com.empmgmt.model.Employee;
import com.empmgmt.model.EmployeeStatus;
import com.empmgmt.model.OnboardingTask;
import com.empmgmt.repository.*;
import com.empmgmt.service.DashboardSummaryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import static com.empmgmt.model.LeaveStatus.PENDING;

@Service
@RequiredArgsConstructor
public class DashboardSummaryServiceImpl implements DashboardSummaryService {

    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;
    private final LeaveRepository leaveRepo;
    private final JobRepository jobRepo;
    private final ApplicationRepository applicationRepo;
    private final OnboardingTaskRepository onboardingTaskRepo;

    @Override
    public DashboardDTO getDashboardStats() {

        DashboardDTO dto = new DashboardDTO();

        // -----------------------------------------
        //  BASIC KPI
        // -----------------------------------------
        dto.setTotalEmployees(employeeRepo.count());
        dto.setOnboardingActive(3);   // Replace when onboarding flow service ready
        dto.setPendingLeaves(leaveRepo.findByStatus(PENDING).size());
        dto.setOpenJobs(jobRepo.findByActiveTrue().size());

        // -----------------------------------------
        // PIPELINE COUNTS
        // -----------------------------------------
        dto.setAppliedCount((long) applicationRepo.findAll().size());
        dto.setScreeningCount(applicationRepo.findAll().stream().filter(a -> "Shortlisted".equals(a.getStatus())).count());
        dto.setInterviewCount(applicationRepo.findAll().stream().filter(a -> "Interviewing".equals(a.getStatus())).count());
        dto.setOfferCount(applicationRepo.findAll().stream().filter(a -> "Hired".equals(a.getStatus())).count());

        // -----------------------------------------
        // EMPLOYEE GROWTH (LAST 12 MONTHS)
        // -----------------------------------------
        Map<String, Long> growth = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String label = month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            long count = employeeRepo.findAll().stream()
                    .filter(e -> e.getCreatedAt() != null &&
                            e.getCreatedAt().getYear() == month.getYear() &&
                            e.getCreatedAt().getMonth() == month.getMonth())
                    .count();

            growth.put(label, count);
        }

        dto.setMonths(new ArrayList<>(growth.keySet()));
        dto.setEmployeeCounts(new ArrayList<>(growth.values()));

        // -----------------------------------------
        // LEAVE BREAKDOWN (dummy for now)
        // -----------------------------------------
        dto.setLeavesApproved(12);
        dto.setLeavesPending(5);
        dto.setLeavesRejected(3);

        // -----------------------------------------
        // ONBOARDING SUMMARY (dummy)
        // -----------------------------------------
        dto.setOnboardingList(List.of(
                new OnboardingSummaryDTO(1L, "John Doe", "john@example.com", 80),
                new OnboardingSummaryDTO(2L, "Lisa Ray", "lisa@example.com", 55)
        ));

        return dto;
    }
}
