package com.empmgmt.service;

import com.empmgmt.model.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    Attendance clockIn(Long employeeId);

    Attendance clockOut(Long employeeId);

    List<Attendance> getEmployeeAttendance(Long employeeId);

    List<Attendance> getAllAttendance();

    List<Attendance> filter(Long employeeId, LocalDate date);

    List<Attendance> filterAttendance(Long empId, LocalDate date);
}

