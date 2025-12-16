package com.empmgmt.repository;

import com.empmgmt.model.Employee;
import com.empmgmt.model.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // =====================================================
    // BASIC CHECKS
    // =====================================================

    boolean existsByEmail(String email);

    Optional<Employee> findByEmail(String email);

    // =====================================================
    // SIMPLE FILTERS
    // =====================================================

    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);

    Page<Employee> findByStatusAndDepartmentContainingIgnoreCase(
            EmployeeStatus status,
            String department,
            Pageable pageable
    );

    Page<Employee> findByStatusAndDepartmentContainingIgnoreCaseAndFirstNameContainingIgnoreCase(
            EmployeeStatus status,
            String department,
            String firstName,
            Pageable pageable
    );

    // =====================================================
    // SAFE GLOBAL SEARCH (CORRECT LOGIC)
    // status AND (firstName OR lastName OR email OR department)
    // =====================================================

    @Query("""
        SELECT e
        FROM Employee e
        WHERE e.status = :status
          AND (
                e.firstName   LIKE %:keyword%
             OR e.lastName    LIKE %:keyword%
             OR e.email       LIKE %:keyword%
             OR e.department  LIKE %:keyword%
          )
    """)
    Page<Employee> searchByStatus(
            @Param("status") EmployeeStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
