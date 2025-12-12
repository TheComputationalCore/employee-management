package com.empmgmt.service.impl;

import com.empmgmt.model.*;
import com.empmgmt.repository.EmployeeRepository;
import com.empmgmt.repository.LeaveBalanceRepository;
import com.empmgmt.repository.LeaveRepository;
import com.empmgmt.service.LeaveAnalyticsService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveAnalyticsServiceImpl implements LeaveAnalyticsService {

    private final LeaveRepository leaveRepo;
    private final LeaveBalanceRepository balanceRepo;
    private final EmployeeRepository employeeRepo;

    /* ============================================================
       LEAVE DISTRIBUTION BY TYPE
    ============================================================ */
    @Override
    public Map<String, Integer> getLeaveTypeDistribution() {

        List<LeaveRequest> all = leaveRepo.findAll();

        return Arrays.stream(LeaveType.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        t -> (int) all.stream().filter(l -> l.getType() == t).count()
                ));
    }

    /* ============================================================
       MONTHLY LEAVE COUNT
    ============================================================ */
    @Override
    public Map<String, Integer> getMonthlyLeaveCount() {

        Map<String, Integer> result = new LinkedHashMap<>();

        // Initialize all 12 months with 0
        for (Month m : Month.values()) {
            result.put(m.name(), 0);
        }

        leaveRepo.findAll().forEach(l -> {
            String month = l.getStartDate().getMonth().name();
            result.put(month, result.get(month) + 1);
        });

        return result;
    }

    /* ============================================================
       LEAVE COUNT BY DEPARTMENT
    ============================================================ */
    @Override
    public Map<String, Integer> getDepartmentLeaveStats() {

        Map<String, Integer> map = new LinkedHashMap<>();

        // Initialize departments
        employeeRepo.findAll().forEach(emp -> map.putIfAbsent(emp.getDepartment(), 0));

        // Count leaves by department
        leaveRepo.findAll().forEach(l -> {
            employeeRepo.findById(l.getEmployeeId()).ifPresent(emp -> {
                map.put(emp.getDepartment(), map.get(emp.getDepartment()) + 1);
            });
        });

        return map;
    }

    /* ============================================================
       PENDING LEAVE COUNT
    ============================================================ */
    @Override
    public int getPendingLeaveCount() {
        return (int) leaveRepo.findAll().stream()
                .filter(l -> l.getStatus() == LeaveStatus.PENDING)
                .count();
    }

    /* ============================================================
       LEAVE BALANCE SUMMARY (FINAL FIX)
       Uses your actual fields: totalDays, usedDays, getRemaining()
    ============================================================ */
    @Override
    public Map<String, Integer> getLeaveBalanceSummary(Long employeeId) {

        return balanceRepo.findByEmployeeId(employeeId)
                .stream()
                .collect(Collectors.toMap(
                        b -> b.getType().name(),
                        LeaveBalance::getRemaining   // <-- your correct method
                ));
    }
}
