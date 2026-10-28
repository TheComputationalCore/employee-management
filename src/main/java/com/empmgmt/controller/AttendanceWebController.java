package com.empmgmt.controller;

import com.empmgmt.model.Attendance;
import com.empmgmt.service.AttendanceService;
import com.empmgmt.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/attendance")
public class AttendanceWebController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;

    /* ==========================================================
       EMPLOYEE — MY ATTENDANCE
       ========================================================== */
    @GetMapping("/my")
    public String myAttendance(Model model) {

        // username/email → Employee → employeeId
        Long employeeId = employeeService.getEmployeeIdFromAuth();

        List<Attendance> records =
                attendanceService.getEmployeeAttendance(employeeId);

        model.addAttribute("my", records);
        model.addAttribute("pageTitle", "My Attendance");

        return "attendance/my";
    }

    /* ==========================================================
       EMPLOYEE — CLOCK IN
       ========================================================== */
    @PostMapping("/clock-in")
    public String clockIn() {

        attendanceService.clockIn(employeeService.getEmployeeIdFromAuth());

        return "redirect:/web/attendance/my?clockedIn";
    }

    /* ==========================================================
       EMPLOYEE — CLOCK OUT
       ========================================================== */
    @PostMapping("/clock-out")
    public String clockOut() {

        attendanceService.clockOut(employeeService.getEmployeeIdFromAuth());

        return "redirect:/web/attendance/my?clockedOut";
    }

    /* ==========================================================
       ADMIN / HR — VIEW ALL ATTENDANCE
       ========================================================== */
    @GetMapping
    public String list(@RequestParam(required = false) Long empId,
                       @RequestParam(required = false) LocalDate date,
                       Model model) {

        List<Attendance> list;

        if (empId == null && date == null) {
            list = attendanceService.getAllAttendance();
        } else {
            list = attendanceService.filter(empId, date);
        }

        model.addAttribute("list", list);
        model.addAttribute("empId", empId);
        model.addAttribute("date", date);
        model.addAttribute("pageTitle", "Attendance Records");

        return "attendance/list";
    }
}
