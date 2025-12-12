package com.empmgmt.repository;

import com.empmgmt.model.Attendance;
import com.empmgmt.model.AttendanceStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    List<Attendance> findByEmployeeId(Long employeeId);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByStatus(AttendanceStatus status);

    @Query("SELECT a FROM Attendance a " +
            "WHERE (:empId IS NULL OR a.employeeId = :empId) " +
            "AND (:date IS NULL OR a.date = :date)")
    List<Attendance> filter(@Param("empId") Long empId,
                            @Param("date") LocalDate date);
}
