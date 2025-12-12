package com.empmgmt.service;

import jakarta.servlet.http.HttpServletResponse;

public interface LeaveExportService {

    void exportAllLeavesToExcel(HttpServletResponse response);

    void exportAllLeavesToPdf(HttpServletResponse response);

    void exportEmployeeLeavePdf(Long employeeId, HttpServletResponse response);
}
