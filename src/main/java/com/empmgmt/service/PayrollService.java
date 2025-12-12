package com.empmgmt.service;

import com.empmgmt.dto.PayrollDTO;

import java.util.List;

public interface PayrollService {

    PayrollDTO generatePayroll(Long employeeId, String month, int year);

    List<PayrollDTO> getEmployeePayroll(Long employeeId);

    List<PayrollDTO> getAllPayrolls();

    void markAsPaid(Long payrollId);

}
