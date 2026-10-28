package com.empmgmt.security.service;

import com.empmgmt.model.Employee;
import com.empmgmt.model.EmployeeStatus;
import com.empmgmt.model.Role;
import com.empmgmt.security.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void normalizesLegacyEmployeeRoleAndOnlyEnablesActiveLinkedEmployees() {
        Employee employee = Employee.builder().id(17L).status(EmployeeStatus.ACTIVE).build();
        User user = User.builder().username("legacy-user").role(Role.EMPLOYEE).employee(employee).build();

        CustomUserDetails details = new CustomUserDetails(user);

        assertEquals("ROLE_EMPLOYEE", details.getAuthorities().iterator().next().getAuthority());
        assertTrue(details.isEnabled());
        assertEquals(17L, details.getEmployeeId());
    }

    @Test
    void disablesInactiveOrUnlinkedEmployeeAccounts() {
        User inactive = User.builder().role(Role.ROLE_EMPLOYEE)
                .employee(Employee.builder().status(EmployeeStatus.INACTIVE).build()).build();
        User unlinked = User.builder().role(Role.ROLE_EMPLOYEE).build();

        assertFalse(new CustomUserDetails(inactive).isEnabled());
        assertFalse(new CustomUserDetails(unlinked).isEnabled());
    }

    @Test
    void doesNotDisableHrAccountBecauseItsEmployeeRecordIsInactive() {
        User hr = User.builder().role(Role.ROLE_HR)
                .employee(Employee.builder().status(EmployeeStatus.INACTIVE).build()).build();

        assertTrue(new CustomUserDetails(hr).isEnabled());
    }
}
