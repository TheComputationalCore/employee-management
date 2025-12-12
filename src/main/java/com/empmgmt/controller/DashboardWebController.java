package com.empmgmt.controller;

import com.empmgmt.dto.DashboardDTO;
import com.empmgmt.service.DashboardSummaryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardWebController {

    private final DashboardSummaryService dashboardSummaryService;

    @GetMapping("/web/dashboard")
    public String dashboardView(Model model) {

        DashboardDTO stats = dashboardSummaryService.getDashboardStats();

        model.addAttribute("stats", stats);
        model.addAttribute("onboardingList", stats.getOnboardingList());

        return "dashboard";
    }
}
