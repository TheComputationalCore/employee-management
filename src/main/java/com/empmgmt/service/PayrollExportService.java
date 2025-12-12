package com.empmgmt.service;

import jakarta.servlet.http.HttpServletResponse;

public interface PayrollExportService {

    void exportAllPayrollsExcel(HttpServletResponse response);

    void exportPayrollPdf(Long payrollId, HttpServletResponse response);

    void exportEmployeePayrollPdf(Long employeeId, HttpServletResponse response);
}
