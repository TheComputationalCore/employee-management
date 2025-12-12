package com.empmgmt.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceDTO {

    private Long id;
    private Long employeeId;
    private String cycle;

    private Integer selfRating;
    private String selfComments;

    private Integer managerRating;
    private String managerComments;

    private Integer finalScore;

    private String status;
}
