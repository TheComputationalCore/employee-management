package com.empmgmt.service.impl;

import com.empmgmt.dto.DashboardDTO;
import com.empmgmt.dto.OnboardingSummaryDTO;
import com.empmgmt.model.Employee;
import com.empmgmt.model.OnboardingFlow;
import com.empmgmt.model.OnboardingTask;
import com.empmgmt.repository.*;
import com.empmgmt.service.DashboardSummaryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import static com.empmgmt.model.LeaveStatus.*;

@Service
@RequiredArgsConstructor
public class DashboardSummaryServiceImpl implements DashboardSummaryService {

    private final EmployeeRepository employeeRepo;
    private final LeaveRepository leaveRepo;
    private final JobRepository jobRepo;
    private final ApplicationRepository applicationRepo;
    private final OnboardingTaskRepository onboardingTaskRepo;
    private final OnboardingFlowRepository onboardingFlowRepo;

    @Override
    public DashboardDTO getDashboardStats() {

        DashboardDTO dto = new DashboardDTO();

        // -----------------------------------------
        //  BASIC KPI
        // -----------------------------------------
        dto.setTotalEmployees(employeeRepo.count());
        List<OnboardingFlow> flows = onboardingFlowRepo.findAll();
        dto.setOnboardingActive(flows.stream().filter(flow -> !flow.isCompleted()).count());
        dto.setPendingLeaves(leaveRepo.findByStatus(PENDING).size());
        dto.setOpenJobs(jobRepo.findByActiveTrue().size());

        // -----------------------------------------
        // PIPELINE COUNTS
        // -----------------------------------------
        var applications = applicationRepo.findAll();
        dto.setAppliedCount(applications.stream().filter(a -> "Applied".equals(a.getStatus())).count());
        dto.setScreeningCount(applications.stream().filter(a -> "Shortlisted".equals(a.getStatus())).count());
        dto.setInterviewCount(applications.stream().filter(a -> "Interviewing".equals(a.getStatus())).count());
        dto.setOfferCount(applications.stream().filter(a -> "Hired".equals(a.getStatus())).count());

        // -----------------------------------------
        // EMPLOYEE GROWTH (LAST 12 MONTHS)
        // -----------------------------------------
        Map<String, Long> growth = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        List<Employee> employees = employeeRepo.findAll();

        for (int i = 11; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String label = month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            long count = employees.stream()
                    .filter(e -> e.getCreatedAt() != null &&
                            e.getCreatedAt().getYear() == month.getYear() &&
                            e.getCreatedAt().getMonth() == month.getMonth())
                    .count();

            growth.put(label, count);
        }

        dto.setMonths(new ArrayList<>(growth.keySet()));
        dto.setEmployeeCounts(new ArrayList<>(growth.values()));

        // -----------------------------------------
        // LEAVE BREAKDOWN
        // -----------------------------------------
        dto.setLeavesApproved(leaveRepo.findByStatus(APPROVED).size());
        dto.setLeavesPending(leaveRepo.findByStatus(PENDING).size());
        dto.setLeavesRejected(leaveRepo.findByStatus(REJECTED).size());

        // -----------------------------------------
        // ONBOARDING SUMMARY
        // -----------------------------------------
        List<OnboardingSummaryDTO> onboardingSummaries = flows.stream()
                .filter(flow -> !flow.isCompleted())
                .map(flow -> employeeRepo.findById(flow.getEmployeeId())
                        .map(employee -> {
                            List<OnboardingTask> tasks = onboardingTaskRepo.findByEmployeeId(employee.getId());
                            int progress = tasks.isEmpty() ? 0 : (int) Math.round(
                                    tasks.stream().filter(OnboardingTask::isCompleted).count() * 100.0 / tasks.size());
                            return new OnboardingSummaryDTO(flow.getId(),
                                    employee.getFirstName() + " " + employee.getLastName(),
                                    employee.getEmail(), progress);
                        })
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
        dto.setOnboardingList(onboardingSummaries);

        return dto;
    }
}
