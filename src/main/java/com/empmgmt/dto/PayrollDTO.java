package com.empmgmt.dto;

import lombok.*;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollDTO {

    private Long id;
    private Long employeeId;

    private String month;
    private int year;

    private Double baseSalary;
    private Double allowances;
    private Double deductions;
    private Double netPay;

    private boolean paid;
    private String paymentDate;
}

