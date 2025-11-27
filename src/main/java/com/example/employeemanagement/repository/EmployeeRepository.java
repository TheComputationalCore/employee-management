package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
        SELECT e FROM Employee e
        WHERE 
            LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(e.department) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(e.position) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    Page<Employee> search(String keyword, Pageable pageable);
}
