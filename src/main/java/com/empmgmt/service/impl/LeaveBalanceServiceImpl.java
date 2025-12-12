package com.empmgmt.service.impl;

import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.model.LeaveBalance;
import com.empmgmt.model.LeaveType;
import com.empmgmt.repository.LeaveBalanceRepository;
import com.empmgmt.service.LeaveBalanceService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository repo;

    @Override
    public List<LeaveBalance> getBalancesForEmployee(Long employeeId) {
        return repo.findByEmployeeId(employeeId);
    }

    @Override
    public LeaveBalance setLeaveBalance(Long employeeId, LeaveType type, Integer totalDays) {

        LeaveBalance balance = repo.findByEmployeeIdAndType(employeeId, type)
                .orElse(LeaveBalance.builder()
                        .employeeId(employeeId)
                        .type(type)
                        .build()
                );

        balance.setTotalDays(totalDays);
        balance.setUsedDays(0);

        return repo.save(balance);
    }

    @Override
    public void deductLeave(Long employeeId, LeaveType type, int days) {

        LeaveBalance balance = repo.findByEmployeeIdAndType(employeeId, type)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        int remaining = balance.getTotalDays() - balance.getUsedDays();
        if (remaining < days) {
            throw new IllegalArgumentException("Not enough leave balance");
        }

        balance.setUsedDays(balance.getUsedDays() + days);
        repo.save(balance);
    }

    @Override
    public int getRemainingDays(Long employeeId, LeaveType type) {

        LeaveBalance balance = repo.findByEmployeeIdAndType(employeeId, type)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        return balance.getTotalDays() - balance.getUsedDays();
    }
}
