package com.example.employeemanagement.mapper;

import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.dto.EmployeeResponseDTO;
import com.example.employeemanagement.dto.EmployeeWebDTO;
import com.example.employeemanagement.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    /* ===========================================================
       ENTITY → RESPONSE DTO (API)
    ============================================================ */
    public EmployeeResponseDTO toResponseDTO(Employee employee) {
        if (employee == null) return null;

        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .fullName(employee.getFirstName() + " " + employee.getLastName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .salary(employee.getSalary())
                .build();
    }

    /* ===========================================================
       REQUEST DTO → ENTITY (API CREATE)
    ============================================================ */
    public Employee toEntity(EmployeeRequestDTO req) {
        if (req == null) return null;

        Employee e = new Employee();
        e.setFirstName(req.getFirstName());
        e.setLastName(req.getLastName());
        e.setEmail(req.getEmail());
        e.setPhoneNumber(req.getPhoneNumber());
        e.setDepartment(req.getDepartment());
        e.setPosition(req.getPosition());
        e.setSalary(req.getSalary());
        return e;
    }

    /* ===========================================================
       UPDATE (API UPDATE)
    ============================================================ */
    public void updateEntity(Employee employee, EmployeeRequestDTO req) {
        if (employee == null || req == null) return;

        employee.setFirstName(req.getFirstName());
        employee.setLastName(req.getLastName());
        employee.setEmail(req.getEmail());
        employee.setPhoneNumber(req.getPhoneNumber());
        employee.setDepartment(req.getDepartment());
        employee.setPosition(req.getPosition());
        employee.setSalary(req.getSalary());
    }

    /* ===========================================================
       ENTITY → WEB DTO (Thymeleaf UI)
    ============================================================ */
    public EmployeeWebDTO toWebDTO(Employee employee) {
        if (employee == null) return null;

        return EmployeeWebDTO.builder()
                .id(employee.getId())
                .fullName(employee.getFirstName() + " " + employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .salary(employee.getSalary())
                .build();
    }
}
