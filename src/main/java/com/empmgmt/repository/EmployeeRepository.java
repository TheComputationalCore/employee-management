package com.empmgmt.repository;

import com.empmgmt.model.Employee;
import com.empmgmt.model.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /* =========================================================
       BASIC LOOKUPS
    ========================================================= */

    boolean existsByEmail(String email);

    Optional<Employee> findByEmail(String email);

    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);

    /* =========================================================
       ACTIVE EMPLOYEE SEARCH (SAFE FOR POSTGRES + NEON)
       ✔ No LOWER() on nullable params
       ✔ No BYTEA binding
       ✔ NULL & empty-safe
    ========================================================= */
    @Query("""
        SELECT e FROM Employee e
        WHERE e.status = com.empmgmt.model.EmployeeStatus.ACTIVE
        AND (
            :search IS NULL OR
            LOWER(e.firstName) LIKE CONCAT('%', LOWER(:search), '%') OR
            LOWER(e.lastName)  LIKE CONCAT('%', LOWER(:search), '%') OR
            LOWER(e.email)     LIKE CONCAT('%', LOWER(:search), '%')
        )
        AND (:department IS NULL OR e.department = :department)
    """)
    Page<Employee> searchActiveEmployees(
            String search,
            String department,
            Pageable pageable
    );
}
