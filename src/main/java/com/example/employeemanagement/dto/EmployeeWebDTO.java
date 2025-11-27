package com.example.employeemanagement.dto;

import lombok.Data;

@Data
public class EmployeeWebDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String department;

    private String position;

    private Double salary;
}
