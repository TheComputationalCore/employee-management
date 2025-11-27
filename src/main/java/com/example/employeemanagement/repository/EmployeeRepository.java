package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /* ===========================================================
       BASIC FINDERS
    ============================================================ */

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    /* ===========================================================
       FILTER QUERIES
       (Supports dashboard + search + table filters)
    ============================================================ */

    List<Employee> findByDepartment(String department);

    List<Employee> findByPosition(String position);
}
