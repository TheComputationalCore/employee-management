package com.empmgmt.repository;

import com.empmgmt.model.LeaveBalance;
import com.empmgmt.model.LeaveType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    Optional<LeaveBalance> findByEmployeeIdAndType(Long employeeId, LeaveType type);

    List<LeaveBalance> findByEmployeeId(Long employeeId);
}
