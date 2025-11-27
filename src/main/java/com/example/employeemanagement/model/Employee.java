package com.thecomputationalcore.employeemanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(
    name = "employees",
    indexes = {
        @Index(name = "idx_employee_email", columnList = "email")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "{employee.firstName.required}")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "{employee.lastName.required}")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "{employee.email.required}")
    @Email(message = "{employee.email.invalid}")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String department;

    private String position;

    @PositiveOrZero(message = "{employee.salary.positive}")
    private Double salary;
}
