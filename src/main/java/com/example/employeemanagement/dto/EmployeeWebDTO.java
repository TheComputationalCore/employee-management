package com.example.employeemanagement.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWebDTO {

    private Long id;

    private String fullName;

    private String email;

    private String department;

    private String position;

    private Double salary;
}
