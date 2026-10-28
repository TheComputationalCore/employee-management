package com.empmgmt.service.impl;

import com.empmgmt.dto.EmployeeDTO;
import com.empmgmt.dto.EmployeeKPIDTO;
import com.empmgmt.model.EmployeeKPI;
import com.empmgmt.repository.EmployeeKPIRepository;
import com.empmgmt.service.EmployeeService;
import com.empmgmt.service.KPIExportService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

// ====== OPENPDF ======
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

// ====== APACHE POI (EXCEL) ======
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
@RequiredArgsConstructor
public class KPIExportServiceImpl implements KPIExportService {

    private final EmployeeKPIRepository kpiRepo;
    private final EmployeeService employeeService;

    // ======================================================
    // PDF EXPORT — OpenPDF Version
    // ======================================================
    @Override
    public void exportEmployeeKpiPdf(Long employeeId, HttpServletResponse response) throws Exception {

        EmployeeDTO emp = employeeService.getEmployee(employeeId);

        List<EmployeeKPIDTO> list = kpiRepo.findByEmployeeId(employeeId)
                .stream()
                .map(k -> EmployeeKPIDTO.builder()
                        .employeeId(k.getEmployeeId())
                        .kpiId(k.getKpiId())
                        .id(k.getId())
                        .targetValue(k.getTargetValue())
                        .achievedSelf(k.getAchievedSelf())
                        .achievedManager(k.getAchievedManager())
                        .finalScore(k.getFinalScore())
                        .build())
                .toList();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=employee_kpi_" + employeeId + ".pdf");

        Document doc = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        // Title
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Employee KPI Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        // Employee Info
        doc.add(new Paragraph(
                emp.getFirstName() + " " + emp.getLastName() +
                        " | " + emp.getDepartment() +
                        " | " + emp.getPosition()
        ));
        doc.add(new Paragraph("\n"));

        // Table
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        addHeader(table, "KPI ID");
        addHeader(table, "Target");
        addHeader(table, "Self");
        addHeader(table, "Manager");
        addHeader(table, "Final Score");

        for (EmployeeKPIDTO k : list) {
            table.addCell(String.valueOf(k.getKpiId()));
            table.addCell(String.valueOf(k.getTargetValue()));
            table.addCell(String.valueOf(k.getAchievedSelf()));
            table.addCell(String.valueOf(k.getAchievedManager()));
            table.addCell(String.valueOf(k.getFinalScore()));
        }

        doc.add(table);
        doc.close();
    }

    // FIXED — no Color.LIGHT_GRAY because OpenPDF does NOT include it
    private void addHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new java.awt.Color(230, 230, 230)); // Light gray
        table.addCell(cell);
    }

    // ======================================================
    // EXCEL EXPORT — Employee KPIs
    // ======================================================
    @Override
    public void exportEmployeeKpiExcel(Long employeeId, HttpServletResponse response) throws IOException {

        EmployeeDTO emp = employeeService.getEmployee(employeeId);
        List<EmployeeKPI> list = kpiRepo.findByEmployeeId(employeeId);

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        response.setHeader("Content-Disposition",
                "attachment; filename=employee_kpi_" + employeeId + ".xlsx");

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("KPIs");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("KPI ID");
        header.createCell(1).setCellValue("Target");
        header.createCell(2).setCellValue("Self Score");
        header.createCell(3).setCellValue("Manager Score");
        header.createCell(4).setCellValue("Final Score");

        int r = 1;
        for (EmployeeKPI k : list) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(k.getKpiId());
            row.createCell(1).setCellValue(k.getTargetValue());
            row.createCell(2).setCellValue(k.getAchievedSelf() == null ? 0 : k.getAchievedSelf());
            row.createCell(3).setCellValue(k.getAchievedManager() == null ? 0 : k.getAchievedManager());
            row.createCell(4).setCellValue(k.getFinalScore() == null ? 0 : k.getFinalScore());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // ======================================================
    // EXCEL EXPORT — All KPIs
    // ======================================================
    @Override
    public void exportAllKpiExcel(HttpServletResponse response) throws IOException {

        List<EmployeeKPI> list = kpiRepo.findAll();

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=kpi_all.xlsx"
        );

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("All KPIs");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Employee ID");
        header.createCell(1).setCellValue("KPI ID");
        header.createCell(2).setCellValue("Target");
        header.createCell(3).setCellValue("Self Score");
        header.createCell(4).setCellValue("Manager Score");
        header.createCell(5).setCellValue("Final Score");

        int r = 1;
        for (EmployeeKPI k : list) {
            Row row = sheet.createRow(r++);
            row.createCell(0).setCellValue(k.getEmployeeId());
            row.createCell(1).setCellValue(k.getKpiId());
            row.createCell(2).setCellValue(k.getTargetValue());
            row.createCell(3).setCellValue(k.getAchievedSelf() == null ? 0 : k.getAchievedSelf());
            row.createCell(4).setCellValue(k.getAchievedManager() == null ? 0 : k.getAchievedManager());
            row.createCell(5).setCellValue(k.getFinalScore() == null ? 0 : k.getFinalScore());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
