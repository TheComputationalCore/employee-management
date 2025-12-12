package com.empmgmt.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "offer_letters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long applicationId;

    private String position;
    private Double salary;
    private LocalDate joiningDate;

    private String filePath;  // Path where PDF is stored
}
