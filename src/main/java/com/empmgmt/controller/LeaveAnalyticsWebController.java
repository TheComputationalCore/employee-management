package com.empmgmt.controller;

import com.empmgmt.service.LeaveAnalyticsService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/leave-analytics")
public class LeaveAnalyticsWebController {

    private final LeaveAnalyticsService analyticsService;

    @GetMapping
    public String analytics(Model model) {

        model.addAttribute("typeDist", analyticsService.getLeaveTypeDistribution());
        model.addAttribute("monthly", analyticsService.getMonthlyLeaveCount());
        model.addAttribute("deptStats", analyticsService.getDepartmentLeaveStats());
        model.addAttribute("pending", analyticsService.getPendingLeaveCount());

        return "leave/analytics";
    }
}
