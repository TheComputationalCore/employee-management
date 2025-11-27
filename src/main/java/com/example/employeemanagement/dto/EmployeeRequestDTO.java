package com.example.employeemanagement.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDTO {

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String department;

    private String position;

    private Double salary;
}
