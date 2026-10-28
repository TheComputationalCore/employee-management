package com.empmgmt.service.impl;

import com.empmgmt.model.Employee;
import com.empmgmt.model.Payroll;
import com.empmgmt.repository.EmployeeRepository;
import com.empmgmt.repository.PayrollRepository;
import com.empmgmt.service.PayrollExportService;

import com.lowagie.text.Font;
import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

// ==== OpenPDF ====
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.Color;

@Service
@RequiredArgsConstructor
public class PayrollExportServiceImpl implements PayrollExportService {

    private final PayrollRepository payrollRepo;
    private final EmployeeRepository employeeRepo;

    // ======================================================
    // EXCEL EXPORT (unchanged)
    // ======================================================
    @Override
    public void exportAllPayrollsExcel(HttpServletResponse response) {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Payroll Report");
            Row header = sheet.createRow(0);

            String[] cols = {"Employee ID", "Month", "Base Salary",
                    "Allowances", "Deductions", "Net Pay", "Paid"};

            for (int i = 0; i < cols.length; i++)
                header.createCell(i).setCellValue(cols[i]);

            List<Payroll> list = payrollRepo.findAll();
            int idx = 1;

            for (Payroll p : list) {
                Row row = sheet.createRow(idx++);
                row.createCell(0).setCellValue(p.getEmployeeId());
                row.createCell(1).setCellValue(p.getMonth() + "-" + p.getYear());
                row.createCell(2).setCellValue(p.getBaseSalary());
                row.createCell(3).setCellValue(p.getAllowances());
                row.createCell(4).setCellValue(p.getDeductions());
                row.createCell(5).setCellValue(p.getNetPay());
                row.createCell(6).setCellValue(p.isPaid() ? "Yes" : "No");
            }

            for (int i = 0; i < cols.length; i++)
                sheet.autoSizeColumn(i);

            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );
            response.setHeader("Content-Disposition", "attachment; filename=payroll.xlsx");

            workbook.write(response.getOutputStream());

        } catch (IOException e) {
            throw new RuntimeException("Excel generation error", e);
        }
    }

    // ======================================================
    // SINGLE PAYSLIP (OpenPDF version)
    // ======================================================
    @Override
    public void exportPayrollPdf(Long payrollId, HttpServletResponse response) {

        Payroll p = payrollRepo.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll not found"));

        Employee emp = employeeRepo.findById(p.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        try {

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=payslip_" + payrollId + ".pdf");

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, response.getOutputStream());
            doc.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, Color.BLACK);
            Paragraph heading = new Paragraph("PAYSLIP", titleFont);
            heading.setAlignment(Element.ALIGN_CENTER);
            heading.setSpacingAfter(20);
            doc.add(heading);

            // Employee Info Table
            PdfPTable empTable = new PdfPTable(2);
            empTable.setWidthPercentage(100);

            addRow(empTable, "Employee", emp.getFirstName() + " " + emp.getLastName());
            addRow(empTable, "Email", emp.getEmail());
            addRow(empTable, "Department", emp.getDepartment());
            addRow(empTable, "Payslip Month", p.getMonth() + " " + p.getYear());

            doc.add(empTable);
            doc.add(new Paragraph("\n"));

            // Salary Table
            PdfPTable salaryTable = new PdfPTable(2);
            salaryTable.setWidthPercentage(100);

            addHeader(salaryTable, "Description");
            addHeader(salaryTable, "Amount");

            addRow(salaryTable, "Base Salary", String.format("%.2f", p.getBaseSalary()));
            addRow(salaryTable, "Allowances", String.format("%.2f", p.getAllowances()));
            addRow(salaryTable, "Deductions", String.format("%.2f", p.getDeductions()));

            doc.add(salaryTable);
            doc.add(new Paragraph("\n"));

            // Net Pay Section
            Font netFont = new Font(Font.HELVETICA, 14, Font.BOLD, Color.BLACK);
            Paragraph netPay = new Paragraph("Net Pay: " + p.getNetPay(), netFont);
            netPay.setAlignment(Element.ALIGN_RIGHT);
            doc.add(netPay);

            doc.close();

        } catch (Exception e) {
            throw new RuntimeException("PDF generation error", e);
        }
    }

    // ======================================================
    // MULTIPLE PAYSLIPS FOR EMPLOYEE (OpenPDF)
    // ======================================================
    @Override
    public void exportEmployeePayrollPdf(Long employeeId, HttpServletResponse response) {

        List<Payroll> list = payrollRepo.findByEmployeeId(employeeId);

        if (list.isEmpty())
            throw new RuntimeException("No payroll records");

        try {

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=employee_payslips_" + employeeId + ".pdf");

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, response.getOutputStream());
            doc.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLACK);

            for (Payroll p : list) {

                Paragraph title = new Paragraph(
                        "Payslip - " + p.getMonth() + " " + p.getYear(),
                        titleFont
                );
                title.setSpacingAfter(10);
                doc.add(title);

                doc.add(new Paragraph("Net Pay: " + p.getNetPay()));
                doc.add(new Paragraph("\n\n"));
            }

            doc.close();

        } catch (Exception e) {
            throw new RuntimeException("PDF error", e);
        }
    }

    // ======================================================
    // Helper Methods
    // ======================================================
    private void addHeader(PdfPTable table, String text) {
        Font headFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(text, headFont));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addRow(PdfPTable table, String key) {
        addRow(table, key, "");
    }

    private void addRow(PdfPTable table, String key, String value) {
        PdfPCell k = new PdfPCell(new Phrase(key, new Font(Font.HELVETICA, 12, Font.BOLD)));
        k.setBorder(Rectangle.NO_BORDER);
        k.setPadding(5);

        PdfPCell v = new PdfPCell(new Phrase(value, new Font(Font.HELVETICA, 12)));
        v.setBorder(Rectangle.NO_BORDER);
        v.setPadding(5);

        table.addCell(k);
        table.addCell(v);
    }
}
