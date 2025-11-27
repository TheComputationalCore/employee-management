package com.thecomputationalcore.employeemanagement.repository;

import com.thecomputationalcore.employeemanagement.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * UNIVERSAL SEARCH:
     * Searches across firstName, lastName, email, department, position (CASE-INSENSITIVE)
     * Pageable supports:
     *   - Pagination
     *   - Sorting
     */
    @Query("""
        SELECT e FROM Employee e
        WHERE 
            LOWER(e.firstName)   LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(e.lastName)    LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(e.email)       LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(e.department)  LIKE LOWER(CONCAT('%', :search, '%')) OR
            LOWER(e.position)    LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    Page<Employee> searchAll(String search, Pageable pageable);


    /**
     * Fetch all (no search filter)
     */
    @Query("SELECT e FROM Employee e")
    Page<Employee> findAllEmployees(Pageable pageable);

}
