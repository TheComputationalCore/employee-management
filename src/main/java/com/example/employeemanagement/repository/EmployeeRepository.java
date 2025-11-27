package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /* ============================================================
       SEARCH — used for pagination + search bar
       ============================================================ */
    @Query("""
            SELECT e FROM Employee e
            WHERE LOWER(e.firstName)   LIKE :keyword
               OR LOWER(e.lastName)    LIKE :keyword
               OR LOWER(e.email)       LIKE :keyword
               OR LOWER(e.department)  LIKE :keyword
               OR LOWER(e.position)    LIKE :keyword
            """)
    Page<Employee> searchEmployees(String keyword, Pageable pageable);

    /* ============================================================
       UNIQUENESS CHECK
       ============================================================ */
    boolean existsByEmail(String email);


    /* ============================================================
       DASHBOARD COUNTERS
       ============================================================ */
    @Query("SELECT COUNT(DISTINCT e.department) FROM Employee e WHERE e.department IS NOT NULL")
    long countDistinctDepartments();

    @Query("SELECT COUNT(DISTINCT e.position) FROM Employee e WHERE e.position IS NOT NULL")
    long countDistinctPositions();
}
