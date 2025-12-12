package com.empmgmt.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "payroll")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private String month;
    private int year;

    private Double baseSalary;
    private Double allowances;
    private Double deductions;
    private Double netPay;

    private boolean paid;
    private LocalDate paymentDate;

    private LocalDate generatedDate;
}
