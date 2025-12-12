package com.empmgmt.controller;

import com.empmgmt.model.LeaveBalance;
import com.empmgmt.model.LeaveType;
import com.empmgmt.service.EmployeeService;
import com.empmgmt.service.LeaveBalanceService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/leave-balance")
public class LeaveBalanceWebController {

    private final LeaveBalanceService balanceService;
    private final EmployeeService employeeService;

    @GetMapping("/{employeeId}")
    public String viewEmployeeBalance(@PathVariable Long employeeId, Model model) {

        model.addAttribute("employee", employeeService.getEmployee(employeeId));
        model.addAttribute("balances", balanceService.getBalancesForEmployee(employeeId));
        model.addAttribute("types", LeaveType.values());

        return "leave/balance";
    }

    @PostMapping("/set")
    public String setBalance(
            @RequestParam Long employeeId,
            @RequestParam LeaveType type,
            @RequestParam Integer totalDays
    ) {
        balanceService.setLeaveBalance(employeeId, type, totalDays);
        return "redirect:/web/leave-balance/" + employeeId + "?updated";
    }
}
