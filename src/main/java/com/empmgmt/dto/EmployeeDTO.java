package com.empmgmt.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {

    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email")
    @NotBlank
    private String email;

    @Size(min = 10, max = 15, message = "Phone number invalid")
    private String phone;

    @NotBlank(message = "Department required")
    private String department;   // <-- THIS IS CORRECT

    @NotBlank(message = "Position required")
    private String position;

    @Positive(message = "Salary must be positive")
    private Double salary;
}
