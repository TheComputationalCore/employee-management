package com.empmgmt.controller;

import com.empmgmt.model.LeaveRequest;
import com.empmgmt.model.LeaveType;
import com.empmgmt.service.EmployeeService;
import com.empmgmt.service.LeaveService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/leave")
public class LeaveWebController {

    private final LeaveService leaveService;
    private final EmployeeService employeeService;

    /* -------------------------------
     * EMPLOYEE APPLY LEAVE
     * ------------------------------- */
    @GetMapping("/apply")
    public String applyForm(Model model) {
        model.addAttribute("types", LeaveType.values());
        model.addAttribute("leave", new LeaveRequest());
        return "leave/apply";
    }

    @PostMapping("/apply")
    public String applySubmit(@ModelAttribute LeaveRequest leave) {

        Long employeeId = employeeService.getEmployeeIdFromAuth();
        leave.setEmployeeId(employeeId);

        leaveService.applyLeave(leave);
        return "redirect:/web/leave/my?applied";
    }

    /* -------------------------------
     * EMPLOYEE: LEAVE LIST
     * ------------------------------- */
    @GetMapping("/my")
    public String myLeaves(Model model) {

        Long employeeId = employeeService.getEmployeeIdFromAuth();
        model.addAttribute("leaves", leaveService.getMyLeaves(employeeId));

        return "leave/my";
    }

    /* ---------------------------------------
     * EMPLOYEE: LEAVE CALENDAR (MY VIEW)
     * --------------------------------------- */
    @GetMapping("/my/calendar")
    public String myLeaveCalendar(Model model) {

        Long employeeId = employeeService.getEmployeeIdFromAuth();
        List<LeaveRequest> leaves = leaveService.getMyLeaves(employeeId);

        List<Map<String, Object>> events = new ArrayList<>();

        for (LeaveRequest leave : leaves) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", leave.getType().name());
            event.put("start", leave.getStartDate().toString());
            event.put("end", leave.getEndDate().plusDays(1).toString());
            event.put("status", leave.getStatus().name());
            event.put("leaveId", leave.getId());
            events.add(event);
        }

        model.addAttribute("events", events);
        model.addAttribute("pageTitle", "My Leave Calendar");

        return "leave/calendar";
    }

    /* -------------------------------
     * ADMIN + HR: LEAVE LIST
     * ------------------------------- */
    @GetMapping
    public String allLeaves(Model model) {
        model.addAttribute("leaves", leaveService.getAllLeaves());
        return "leave/list";
    }

    /* ---------------------------------------
     * ADMIN + HR: LEAVE CALENDAR (ALL)
     * --------------------------------------- */
    @GetMapping("/calendar")
    public String adminLeaveCalendar(Model model) {

        List<LeaveRequest> leaves = leaveService.getAllLeaves();
        List<Map<String, Object>> events = new ArrayList<>();

        for (LeaveRequest leave : leaves) {
            Map<String, Object> event = new HashMap<>();
            event.put("title", "Emp " + leave.getEmployeeId() + " - " + leave.getType().name());
            event.put("start", leave.getStartDate().toString());
            event.put("end", leave.getEndDate().plusDays(1).toString());
            event.put("status", leave.getStatus().name());
            event.put("leaveId", leave.getId());
            events.add(event);
        }

        model.addAttribute("events", events);
        model.addAttribute("pageTitle", "Leave Calendar");

        return "leave/calendar";
    }

    /* -------------------------------
     * APPROVAL ACTIONS (ADMIN / HR)
     * ------------------------------- */
    @GetMapping("/approve/{id}")
    public String approve(@PathVariable Long id) {
        leaveService.approveLeave(id);
        return "redirect:/web/leave?approved";
    }

    @GetMapping("/reject/{id}")
    public String reject(@PathVariable Long id) {
        leaveService.rejectLeave(id);
        return "redirect:/web/leave?rejected";
    }
}
