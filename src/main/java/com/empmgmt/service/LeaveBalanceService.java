package com.empmgmt.service;

import com.empmgmt.model.LeaveBalance;
import com.empmgmt.model.LeaveType;

import java.util.List;

public interface LeaveBalanceService {

    List<LeaveBalance> getBalancesForEmployee(Long employeeId);

    LeaveBalance setLeaveBalance(Long employeeId, LeaveType type, Integer totalDays);

    void deductLeave(Long employeeId, LeaveType type, int days);

    int getRemainingDays(Long employeeId, LeaveType type);
}
