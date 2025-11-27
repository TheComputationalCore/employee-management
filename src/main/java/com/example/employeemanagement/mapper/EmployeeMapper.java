package com.example.employeemanagement.mapper;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeDTO toDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();

        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setDepartment(employee.getDepartment());
        dto.setPosition(employee.getPosition());
        dto.setSalary(employee.getSalary());

        return dto;
    }

    public Employee toEntity(EmployeeRequestDTO req) {
        Employee employee = new Employee();

        employee.setFirstName(req.getFirstName());
        employee.setLastName(req.getLastName());
        employee.setEmail(req.getEmail());
        employee.setPhoneNumber(req.getPhoneNumber());
        employee.setDepartment(req.getDepartment());
        employee.setPosition(req.getPosition());
        employee.setSalary(req.getSalary());

        return employee;
    }

    public void updateEntity(Employee employee, EmployeeRequestDTO req) {
        employee.setFirstName(req.getFirstName());
        employee.setLastName(req.getLastName());
        employee.setEmail(req.getEmail());
        employee.setPhoneNumber(req.getPhoneNumber());
        employee.setDepartment(req.getDepartment());
        employee.setPosition(req.getPosition());
        employee.setSalary(req.getSalary());
    }
}
