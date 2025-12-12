package com.empmgmt.service.impl;

import com.empmgmt.model.Employee;
import com.empmgmt.model.EmployeeStatus;
import com.empmgmt.repository.DepartmentRepository;
import com.empmgmt.repository.EmployeeRepository;
import com.empmgmt.service.DashboardService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;

    @Override
    public long getTotalEmployees() {
        return employeeRepo.count();
    }

    @Override
    public long getTotalDepartments() {
        return departmentRepo.count();
    }

    @Override
    public Map<String, Long> getEmployeesPerDepartment() {
        Map<String, Long> map = new LinkedHashMap<>();

        employeeRepo.findAll().stream()
                .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                .forEach(e ->
                        map.merge(
                                e.getDepartment(),
                                1L,
                                (oldVal, newVal) -> oldVal + newVal
                        )
                );

        return map;
    }

    @Override
    public List<Map<String, Object>> getRecentEmployees(int limit) {

        List<Map<String, Object>> list = new ArrayList<>();

        employeeRepo.findAll().stream()
                .sorted(Comparator.comparing(Employee::getCreatedAt).reversed())
                .limit(limit)
                .forEach(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", e.getId());
                    m.put("name", e.getFirstName() + " " + e.getLastName());
                    m.put("department", e.getDepartment());
                    list.add(m);
                });

        return list;
    }

    @Override
    public double getAverageSalary() {
        return employeeRepo.findAll().stream()
                .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                .mapToDouble(e -> e.getSalary() == null ? 0 : e.getSalary())
                .average().orElse(0);
    }

    @Override
    public Map<String, Long> getEmployeeGrowthLast12Months() {

        Map<String, Long> result = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);

            String label = month.getMonth()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            long count = employeeRepo.findAll().stream()
                    .filter(e -> e.getCreatedAt() != null &&
                            e.getCreatedAt().getMonth() == month.getMonth() &&
                            e.getCreatedAt().getYear() == month.getYear())
                    .count();

            result.put(label, count);
        }

        return result;
    }

    @Override
    public Map<String, Long> getSalaryDistribution() {

        Map<String, Long> dist = new LinkedHashMap<>();
        dist.put("0 - 25k", 0L);
        dist.put("25k - 50k", 0L);
        dist.put("50k - 75k", 0L);
        dist.put("75k - 100k", 0L);
        dist.put("100k+", 0L);

        employeeRepo.findAll().stream()
                .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                .forEach(e -> {
                    double s = e.getSalary() == null ? 0 : e.getSalary();

                    if (s < 25000)
                        dist.merge("0 - 25k", 1L, (oldVal, newVal) -> oldVal + newVal);
                    else if (s < 50000)
                        dist.merge("25k - 50k", 1L, (oldVal, newVal) -> oldVal + newVal);
                    else if (s < 75000)
                        dist.merge("50k - 75k", 1L, (oldVal, newVal) -> oldVal + newVal);
                    else if (s < 100000)
                        dist.merge("75k - 100k", 1L, (oldVal, newVal) -> oldVal + newVal);
                    else
                        dist.merge("100k+", 1L, (oldVal, newVal) -> oldVal + newVal);
                });

        return dist;
    }

    @Override
    public Map<String, Double> getDepartmentAverageSalary() {

        Map<String, Double> map = new LinkedHashMap<>();

        departmentRepo.findAll().forEach(d -> {

            var employees = employeeRepo.findAll().stream()
                    .filter(e -> d.getName().equals(e.getDepartment()) &&
                            e.getStatus() == EmployeeStatus.ACTIVE)
                    .toList();

            double avg = employees.stream()
                    .mapToDouble(e -> e.getSalary() == null ? 0 : e.getSalary())
                    .average().orElse(0);

            map.put(d.getName(), avg);
        });

        return map;
    }

    @Override
    public List<Map<String, Object>> getTopPaidEmployees(int limit) {

        List<Map<String, Object>> list = new ArrayList<>();

        employeeRepo.findAll().stream()
                .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                .sorted(Comparator.comparingDouble(e -> -(e.getSalary() == null ? 0 : e.getSalary())))
                .limit(limit)
                .forEach(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("name", e.getFirstName() + " " + e.getLastName());
                    m.put("salary", e.getSalary());
                    m.put("department", e.getDepartment());
                    list.add(m);
                });

        return list;
    }
}
