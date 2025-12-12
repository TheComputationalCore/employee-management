package com.empmgmt.service.impl;

import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.model.Attendance;
import com.empmgmt.model.AttendanceStatus;
import com.empmgmt.repository.AttendanceRepository;
import com.empmgmt.service.AttendanceService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository repo;

    @Override
    public Attendance clockIn(Long employeeId) {

        LocalDate today = LocalDate.now();

        return repo.findByEmployeeIdAndDate(employeeId, today).orElseGet(() -> {

            Attendance a = Attendance.builder()
                    .employeeId(employeeId)
                    .date(today)
                    .checkIn(LocalTime.now())
                    .status(AttendanceStatus.PRESENT)
                    .build();

            return repo.save(a);
        });
    }

    @Override
    public Attendance clockOut(Long employeeId) {

        LocalDate today = LocalDate.now();

        Attendance a = repo.findByEmployeeIdAndDate(employeeId, today)
                .orElseThrow(() -> new ResourceNotFoundException("Clock-in missing"));

        a.setCheckOut(LocalTime.now());

        double totalHours = ChronoUnit.MINUTES
                .between(a.getCheckIn(), a.getCheckOut()) / 60.0;

        a.setTotalHours(totalHours);

        return repo.save(a);
    }

    @Override
    public List<Attendance> getEmployeeAttendance(Long employeeId) {
        return repo.findByEmployeeId(employeeId);
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return repo.findAll();
    }

    @Override
    public List<Attendance> filter(Long employeeId, LocalDate date) {

        if (employeeId != null && date != null)
            return repo.findByEmployeeIdAndDate(employeeId, date).map(List::of).orElse(List.of());

        if (employeeId != null)
            return repo.findByEmployeeId(employeeId);

        if (date != null)
            return repo.findByDate(date);

        return repo.findAll();
    }

    @Override
    public List<Attendance> filterAttendance(Long empId, LocalDate date) {
        return List.of();
    }
}
