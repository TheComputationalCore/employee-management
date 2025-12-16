package com.empmgmt.repository;

import com.empmgmt.model.Employee;
import com.empmgmt.model.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    Optional<Employee> findByEmail(String email);

    @Query("""
        SELECT e FROM Employee e
        WHERE e.status = com.empmgmt.model.EmployeeStatus.ACTIVE
        AND (
            :search IS NULL OR
            e.firstName ILIKE CONCAT('%', :search, '%') OR
            e.lastName  ILIKE CONCAT('%', :search, '%') OR
            e.email     ILIKE CONCAT('%', :search, '%')
        )
        AND (:department IS NULL OR e.department = :department)
        """)
    Page<Employee> searchActiveEmployees(
            String search,
            String department,
            Pageable pageable
    );

    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);
}
