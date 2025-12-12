package com.empmgmt.dto;

import com.empmgmt.model.EmployeeStatus;
import lombok.Data;

@Data
public class EmployeeSearchRequest {

    private String search;
    private String department;

    private Integer page = 0;
    private Integer size = 10;
    private String sort = "id,asc";

    private EmployeeStatus status;
}
