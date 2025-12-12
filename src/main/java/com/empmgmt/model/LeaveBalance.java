package com.empmgmt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    @Enumerated(EnumType.STRING)
    private LeaveType type;

    // ⭐ REQUIRED BY LeaveBalanceServiceImpl
    private Integer totalDays = 0;

    // ⭐ REQUIRED BY LeaveBalanceServiceImpl
    private Integer usedDays = 0;

    // Convenience method
    public int getRemaining() {
        return totalDays - usedDays;
    }
}
