package com.empmgmt.controller;

import com.empmgmt.model.Attendance;
import com.empmgmt.service.AttendanceService;

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

    /** EMPLOYEE VIEW */
    @GetMapping("/my")
    public String myAttendance(Authentication auth, Model model) {

        Long empId = Long.parseLong(auth.getName()); // username = employeeId

        model.addAttribute("my", attendanceService.getEmployeeAttendance(empId));
        model.addAttribute("pageTitle", "My Attendance");

        return "attendance/my";
    }

    @PostMapping("/clock-in")
    public String clockIn(Authentication auth) {

        Long empId = Long.parseLong(auth.getName());
        attendanceService.clockIn(empId);

        return "redirect:/web/attendance/my";
    }

    @PostMapping("/clock-out")
    public String clockOut(Authentication auth) {

        Long empId = Long.parseLong(auth.getName());
        attendanceService.clockOut(empId);

        return "redirect:/web/attendance/my";
    }


    /** ADMIN / HR */
    @GetMapping("")
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
