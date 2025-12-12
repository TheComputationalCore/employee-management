package com.empmgmt.security.model;

import com.empmgmt.model.Employee;
import com.empmgmt.model.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    @OneToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public Long getEmployeeId() {
        return (employee != null) ? employee.getId() : null;
    }

}
