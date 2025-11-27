package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    @Query("""
           SELECT e FROM Employee e
           WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(e.department) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(e.position) LIKE LOWER(CONCAT('%', :keyword, '%'))
           """)
    Page<Employee> searchEmployees(String keyword, Pageable pageable);
}
