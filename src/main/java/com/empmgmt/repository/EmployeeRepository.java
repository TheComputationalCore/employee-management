package com.empmgmt.repository;

import com.empmgmt.model.Employee;
import com.empmgmt.model.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    Optional<Employee> findByEmail(String email);

    // =====================================================
    // SAFE SEARCH (NO JPQL, NO LOWER, NO ILIKE)
    // =====================================================

    Page<Employee> findByStatusAndDepartmentContainingIgnoreCaseAndFirstNameContainingIgnoreCase(
            EmployeeStatus status,
            String department,
            String firstName,
            Pageable pageable
    );

    Page<Employee> findByStatusAndFirstNameContainingIgnoreCaseOrStatusAndLastNameContainingIgnoreCaseOrStatusAndEmailContainingIgnoreCase(
            EmployeeStatus status1,
            String firstName,
            EmployeeStatus status2,
            String lastName,
            EmployeeStatus status3,
            String email,
            Pageable pageable
    );

    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);
}
