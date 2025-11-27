package com.example.employeemanagement.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EmployeeRequestDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phoneNumber;

    private String department;

    private String position;

    @PositiveOrZero(message = "Salary must be positive")
    private Double salary;
}
