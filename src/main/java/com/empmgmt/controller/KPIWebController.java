package com.empmgmt.controller;

import com.empmgmt.dto.EmployeeKPIDTO;
import com.empmgmt.service.EmployeeKPIService;
import com.empmgmt.service.EmployeeService;
import com.empmgmt.service.KPIService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/kpi")
public class KPIWebController {

    private final KPIService kpiService;
    private final EmployeeKPIService empKpiService;
    private final EmployeeService employeeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("kpis", kpiService.getAll());
        return "kpi/list";
    }

    @GetMapping("/create")
    public String createForm() {
        return "kpi/create";
    }

    @PostMapping("/create")
    public String create(@RequestParam String name,
                         @RequestParam String description,
                         @RequestParam Double weight) {

        kpiService.create(
                new com.empmgmt.dto.KPIDTO(null, name, description, weight)
        );

        return "redirect:/web/kpi?success";
    }

    @GetMapping("/assign")
    public String assignForm(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("kpis", kpiService.getAll());
        return "kpi/assign";
    }

    @PostMapping("/assign")
    public String assign(
            @RequestParam Long employeeId,
            @RequestParam Long kpiId,
            @RequestParam Double targetValue) {

        empKpiService.assignKPI(
                EmployeeKPIDTO.builder()
                        .employeeId(employeeId)
                        .kpiId(kpiId)
                        .targetValue(targetValue)
                        .build()
        );

        return "redirect:/web/kpi?assigned";
    }

    @GetMapping("/employee/{employeeId}")
    public String employeeKPIs(@PathVariable Long employeeId, Model model) {
        model.addAttribute("list", empKpiService.getEmployeeKPIs(employeeId));
        model.addAttribute("employeeId", employeeId);
        return "kpi/employee-kpis";
    }

    @PostMapping("/self/{id}")
    public String selfScore(@PathVariable Long id, @RequestParam Double achieved) {
        empKpiService.submitSelfScore(id, achieved);
        return "redirect:/web/kpi?selfSubmitted";
    }

    @PostMapping("/manager/{id}")
    public String managerScore(@PathVariable Long id, @RequestParam Double achieved) {
        empKpiService.managerScore(id, achieved);
        return "redirect:/web/kpi?managerSubmitted";
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        // KPIs for all employees
        var all = empKpiService.getEmployeeKPIs(null);

        model.addAttribute("allKPIs", all);

        return "kpi/analytics";
    }
}
