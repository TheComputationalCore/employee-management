package com.empmgmt.security.service;

import com.empmgmt.security.model.User;
import com.empmgmt.model.EmployeeStatus;
import com.empmgmt.model.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Normalize legacy records that stored EMPLOYEE without the ROLE_ prefix.
        String authority = user.getRole() == Role.EMPLOYEE ? "ROLE_EMPLOYEE" : user.getRole().name();
        return List.of(
                new SimpleGrantedAuthority(authority)
        );
    }

    @Override public String getPassword() { return user.getPassword(); }
    @Override public String getUsername() { return user.getUsername(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() {
        if (user.getRole() != Role.EMPLOYEE && user.getRole() != Role.ROLE_EMPLOYEE) {
            return true;
        }
        return user.getEmployee() != null
                && user.getEmployee().getStatus() == EmployeeStatus.ACTIVE;
    }

    public Long getEmployeeId() {
        return user.getEmployeeId();
    }
}
