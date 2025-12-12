package com.empmgmt.service;

import jakarta.servlet.http.HttpServletResponse;

public interface ExportService {

    void exportEmployeesToExcel(HttpServletResponse response);

    void exportEmployeesToPDF(HttpServletResponse response);
}
