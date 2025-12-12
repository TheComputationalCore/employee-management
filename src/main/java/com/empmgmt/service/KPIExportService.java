package com.empmgmt.service;

import jakarta.servlet.http.HttpServletResponse;

public interface KPIExportService {

    void exportEmployeeKpiPdf(Long employeeId, HttpServletResponse response) throws Exception;

    void exportEmployeeKpiExcel(Long employeeId, HttpServletResponse response) throws Exception;

    void exportAllKpiExcel(HttpServletResponse response) throws Exception;
}
