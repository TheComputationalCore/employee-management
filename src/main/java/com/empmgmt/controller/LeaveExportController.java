package com.empmgmt.controller;

import com.empmgmt.service.LeaveExportService;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/export/leave")
public class LeaveExportController {

    private final LeaveExportService exportService;

    @GetMapping("/excel")
    public void allLeavesExcel(HttpServletResponse response) {
        exportService.exportAllLeavesToExcel(response);
    }

    @GetMapping("/pdf")
    public void allLeavesPdf(HttpServletResponse response) {
        exportService.exportAllLeavesToPdf(response);
    }

    @GetMapping("/employee/{id}")
    public void employeeLeavePdf(
            @PathVariable Long id,
            HttpServletResponse response
    ) {
        exportService.exportEmployeeLeavePdf(id, response);
    }
}
