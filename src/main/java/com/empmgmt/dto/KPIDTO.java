package com.empmgmt.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KPIDTO {

    private Long id;
    private String name;
    private String description;
    private Double weight;
}
