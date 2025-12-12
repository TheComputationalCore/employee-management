package com.empmgmt.service.impl;

import com.empmgmt.dto.PayrollDTO;
import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.model.Employee;
import com.empmgmt.model.Payroll;
import com.empmgmt.repository.EmployeeRepository;
import com.empmgmt.repository.PayrollRepository;
import com.empmgmt.service.PayrollService;
import java.util.List;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepo;
    private final EmployeeRepository employeeRepo;


    @Override
    public void markAsPaid(Long id) {
        Payroll p = payrollRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll not found"));

        p.setPaid(true);
        p.setPaymentDate(LocalDate.now());

        payrollRepo.save(p);
    }


    @Override
    public PayrollDTO generatePayroll(Long empId, String month, int year) {

        // Prevent duplicate salary for same month
        payrollRepo.findByEmployeeIdAndMonthAndYear(empId, month, year)
                .ifPresent(p -> {
                    throw new IllegalArgumentException("Payroll already generated for this employee");
                });

        Employee emp = employeeRepo.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        double base = emp.getSalary();
        double allowances = base * 0.10;   // 10% allowance
        double deductions = base * 0.08;   // 8% deduction
        double net = base + allowances - deductions;

        Payroll payroll = Payroll.builder()
                .employeeId(empId)
                .month(month)
                .year(year)
                .baseSalary(base)
                .allowances(allowances)
                .deductions(deductions)
                .netPay(net)
                .generatedDate(LocalDate.now())
                .build();

        payrollRepo.save(payroll);

        return toDTO(payroll);
    }

    @Override
    public List<PayrollDTO> getEmployeePayroll(Long employeeId) {
        return payrollRepo.findByEmployeeId(employeeId)
                .stream().map(this::toDTO).toList();
    }

    @Override
    public List<PayrollDTO> getAllPayrolls() {
        return payrollRepo.findAll().stream().map(this::toDTO).toList();
    }

    private PayrollDTO toDTO(Payroll p) {
        return PayrollDTO.builder()
                .id(p.getId())
                .employeeId(p.getEmployeeId())
                .month(p.getMonth())
                .year(p.getYear())
                .baseSalary(p.getBaseSalary())
                .allowances(p.getAllowances())
                .deductions(p.getDeductions())
                .netPay(p.getNetPay())
                .paid(p.isPaid())
                .paymentDate(p.getPaymentDate() != null ? p.getPaymentDate().toString() : "Not Paid")
                .build();
    }


}
