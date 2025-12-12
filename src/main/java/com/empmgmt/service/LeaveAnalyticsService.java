package com.empmgmt.service;

import java.util.Map;

public interface LeaveAnalyticsService {

    Map<String, Integer> getLeaveTypeDistribution();

    Map<String, Integer> getMonthlyLeaveCount();

    Map<String, Integer> getDepartmentLeaveStats();

    int getPendingLeaveCount();

    Map<String, Integer> getLeaveBalanceSummary(Long employeeId);
}
