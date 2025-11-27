package com.example.employeemanagement.dto;

import lombok.Data;

@Data
public class EmployeeFilterRequest {

    private String search = "";
    private String department = "";
    private String position = "";
    private String sortField = "id";
    private String sortDir = "asc";
}
