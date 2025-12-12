package com.empmgmt.repository;

import com.empmgmt.model.EmployeeKPI;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeKPIRepository extends JpaRepository<EmployeeKPI, Long> {

    List<EmployeeKPI> findByEmployeeId(Long employeeId);
}
