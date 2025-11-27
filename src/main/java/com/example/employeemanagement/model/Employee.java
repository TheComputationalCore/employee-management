package com.example.employeemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    private String phoneNumber;

    private String department;

    private String position;

    @PositiveOrZero(message = "Salary must be positive")
    private Double salary;
}
