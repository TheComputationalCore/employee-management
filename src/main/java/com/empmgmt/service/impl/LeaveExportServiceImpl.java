package com.empmgmt.service.impl;

import com.empmgmt.model.LeaveRequest;
import com.empmgmt.model.Employee;
import com.empmgmt.repository.LeaveRepository;
import com.empmgmt.repository.EmployeeRepository;
import com.empmgmt.service.LeaveExportService;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;

// ===== OpenPDF =====
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveExportServiceImpl implements LeaveExportService {

    private final LeaveRepository leaveRepo;
    private final EmployeeRepository employeeRepo;

    /* ===============================================================
       EXCEL EXPORT — Apache POI
       =============================================================== */
    @Override
    public void exportAllLeavesToExcel(HttpServletResponse response) {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Leave Requests");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row header = sheet.createRow(0);
            String[] columns = {
                    "ID", "Employee ID", "Type", "Start Date",
                    "End Date", "Reason", "Status"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            List<LeaveRequest> leaves = leaveRepo.findAll();
            int rowIdx = 1;

            for (LeaveRequest lr : leaves) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(lr.getId());
                row.createCell(1).setCellValue(lr.getEmployeeId());
                row.createCell(2).setCellValue(lr.getType().name());
                row.createCell(3).setCellValue(lr.getStartDate().toString());
                row.createCell(4).setCellValue(lr.getEndDate().toString());
                row.createCell(5).setCellValue(lr.getReason());
                row.createCell(6).setCellValue(lr.getStatus().name());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=leave_requests.xlsx"
            );

            workbook.write(response.getOutputStream());

        } catch (IOException e) {
            throw new RuntimeException("Error generating Excel file", e);
        }
    }

    /* ===============================================================
       PDF EXPORT — OpenPDF
       =============================================================== */
    @Override
    public void exportAllLeavesToPdf(HttpServletResponse response) {

        try {
            response.setContentType("application/pdf");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=leave_requests.pdf"
            );

            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, response.getOutputStream());
            doc.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Leave Request Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            doc.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);

            String[] headers = {
                    "ID", "Employee ID", "Type", "Start Date",
                    "End Date", "Reason", "Status"
            };

            addPdfHeaderRow(table, headers);

            List<LeaveRequest> leaves = leaveRepo.findAll();

            for (LeaveRequest lr : leaves) {
                table.addCell(String.valueOf(lr.getId()));
                table.addCell(String.valueOf(lr.getEmployeeId()));
                table.addCell(lr.getType().name());
                table.addCell(lr.getStartDate().toString());
                table.addCell(lr.getEndDate().toString());
                table.addCell(lr.getReason());
                table.addCell(lr.getStatus().name());
            }

            doc.add(table);
            doc.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    private void addPdfHeaderRow(PdfPTable table, String[] headers) {
        Font font = new Font(Font.HELVETICA, 12, Font.BOLD);
        Color lightGray = new Color(220, 220, 220);

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(lightGray);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    /* ===============================================================
       EMPLOYEE LEAVE SUMMARY — OpenPDF
       =============================================================== */
    @Override
    public void exportEmployeeLeavePdf(Long employeeId, HttpServletResponse response) {

        try {
            Employee emp = employeeRepo.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            List<LeaveRequest> leaves = leaveRepo.findByEmployeeId(employeeId);

            response.setContentType("application/pdf");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=employee_leave_summary_" + employeeId + ".pdf"
            );

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, response.getOutputStream());
            doc.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph(
                    "Leave Summary — " + emp.getFirstName() + " " + emp.getLastName(),
                    titleFont
            );
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);
            doc.add(new Paragraph("\n"));

            // Employee info
            doc.add(new Paragraph("Employee ID: " + emp.getId()));
            doc.add(new Paragraph("Email: " + emp.getEmail()));
            doc.add(new Paragraph("Department: " + emp.getDepartment()));
            doc.add(new Paragraph("Position: " + emp.getPosition()));
            doc.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            addPdfHeaderRow(table, new String[]{
                    "Type", "Start Date", "End Date", "Reason", "Status"
            });

            for (LeaveRequest lr : leaves) {
                table.addCell(lr.getType().name());
                table.addCell(lr.getStartDate().toString());
                table.addCell(lr.getEndDate().toString());
                table.addCell(lr.getReason());
                table.addCell(lr.getStatus().name());
            }

            doc.add(table);
            doc.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating employee PDF", e);
        }
    }
}
