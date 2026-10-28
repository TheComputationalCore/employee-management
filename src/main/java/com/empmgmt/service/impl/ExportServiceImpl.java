package com.empmgmt.service.impl;

import com.empmgmt.model.Employee;
import com.empmgmt.model.EmployeeStatus;
import com.empmgmt.repository.EmployeeRepository;
import com.empmgmt.service.ExportService;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

// ===== OpenPDF =====
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;

import java.awt.Color;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final EmployeeRepository repo;

    // ==========================================================
    // EXCEL EXPORT (POI)
    // ==========================================================
    @Override
    public void exportEmployeesToExcel(HttpServletResponse response) {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Employees");

            Row header = sheet.createRow(0);
            String[] columns = {
                    "ID", "First Name", "Last Name", "Email",
                    "Phone", "Department", "Position", "Salary", "Status"
            };

            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            List<Employee> employees = repo.findAll().stream()
                    .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                    .toList();

            int rowIdx = 1;

            for (Employee e : employees) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(e.getId());
                row.createCell(1).setCellValue(e.getFirstName());
                row.createCell(2).setCellValue(e.getLastName());
                row.createCell(3).setCellValue(e.getEmail());
                row.createCell(4).setCellValue(e.getPhone());
                row.createCell(5).setCellValue(e.getDepartment());
                row.createCell(6).setCellValue(e.getPosition());
                row.createCell(7).setCellValue(e.getSalary());
                row.createCell(8).setCellValue(e.getStatus().name());
            }

            response.setContentType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            );
            response.setHeader("Content-Disposition", "attachment; filename=employees.xlsx");

            workbook.write(response.getOutputStream());
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to export Excel", e);
        }
    }

    // ==========================================================
    // PDF EXPORT (OpenPDF)
    // ==========================================================
    @Override
    public void exportEmployeesToPDF(HttpServletResponse response) {

        try {
            response.setContentType("application/pdf");
            response.setHeader(
                    "Content-Disposition",
                    "attachment; filename=employees.pdf"
            );

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLACK);
            Paragraph title = new Paragraph("Employee Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Table Header
            String[] cols = {
                    "ID", "First Name", "Last Name", "Email",
                    "Department", "Position", "Salary"
            };

            PdfPTable table = new PdfPTable(cols.length);
            table.setWidthPercentage(100);

            Font headFont = new Font(Font.HELVETICA, 12, Font.BOLD);

            for (String c : cols) {
                PdfPCell cell = new PdfPCell(new Phrase(c, headFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Rows
            List<Employee> employees = repo.findAll().stream()
                    .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                    .toList();

            for (Employee e : employees) {
                table.addCell(String.valueOf(e.getId()));
                table.addCell(e.getFirstName());
                table.addCell(e.getLastName());
                table.addCell(e.getEmail());
                table.addCell(e.getDepartment());
                table.addCell(e.getPosition());
                table.addCell(String.valueOf(e.getSalary()));
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error creating PDF", e);
        }
    }
}
