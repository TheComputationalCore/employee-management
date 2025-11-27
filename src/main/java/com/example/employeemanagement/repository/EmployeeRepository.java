package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /* -----------------------------------------------------------
       SEARCH (name, email, department, position)
    ------------------------------------------------------------ */
    @Query("""
            SELECT e FROM Employee e
            WHERE LOWER(e.firstName)   LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.lastName)    LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.email)       LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.department)  LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.position)    LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<Employee> search(String keyword);


    /* -----------------------------------------------------------
       UNIQUE EMAIL CHECK
    ------------------------------------------------------------ */
    boolean existsByEmail(String email);


    /* -----------------------------------------------------------
       DASHBOARD COUNTERS
    ------------------------------------------------------------ */

    @Query("SELECT COUNT(e) FROM Employee e")
    long countEmployees();

    @Query("SELECT COUNT(DISTINCT e.department) FROM Employee e WHERE e.department IS NOT NULL AND e.department <> ''")
    long countDepartments();

    @Query("SELECT COUNT(DISTINCT e.position) FROM Employee e WHERE e.position IS NOT NULL AND e.position <> ''")
    long countPositions();
}
