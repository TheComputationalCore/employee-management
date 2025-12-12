package com.empmgmt.service;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    long getTotalEmployees();

    long getTotalDepartments();

    Map<String, Long> getEmployeesPerDepartment();

    List<Map<String, Object>> getRecentEmployees(int limit);

    double getAverageSalary();

    Map<String, Long> getEmployeeGrowthLast12Months();

    Map<String, Long> getSalaryDistribution();

    Map<String, Double> getDepartmentAverageSalary();

    List<Map<String, Object>> getTopPaidEmployees(int limit);
}
