package com.empmgmt.security.service;

import com.empmgmt.model.Employee;
import com.empmgmt.model.EmployeeStatus;
import com.empmgmt.model.Role;
import com.empmgmt.repository.EmployeeRepository;
import com.empmgmt.security.model.User;
import com.empmgmt.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PasswordSetupServiceTest {

    private UserRepository userRepository;
    private EmployeeRepository employeeRepository;
    private PasswordEncoder passwordEncoder;
    private PasswordSetupService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new PasswordSetupService(userRepository, employeeRepository, passwordEncoder);
        when(passwordEncoder.encode(any())).thenReturn("encoded-secret");
    }

    @Test
    void createsLoginForExistingActiveEmployeeWithoutAccount() {
        Employee employee = Employee.builder().id(42L).status(EmployeeStatus.ACTIVE).build();
        when(employeeRepository.findById(42L)).thenReturn(Optional.of(employee));
        when(userRepository.findByEmployee_Id(42L)).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("42")).thenReturn(false);

        PasswordSetupService.SetupCode setupCode = service.issueSetupCode(42L);

        assertEquals("42", setupCode.username());
        assertNotNull(setupCode.code());
        verify(userRepository).save(argThat(user -> "42".equals(user.getUsername())
                && user.getRole() == Role.ROLE_EMPLOYEE
                && user.getEmployee() == employee
                && user.getSetupTokenHash() != null));
    }

    @Test
    void setupCodeShowsExistingUsernameAndNormalizesLegacyRole() {
        Employee employee = Employee.builder().id(9L).status(EmployeeStatus.ACTIVE).build();
        User user = User.builder().username("employee-jane").role(Role.EMPLOYEE).employee(employee).build();
        when(employeeRepository.findById(9L)).thenReturn(Optional.of(employee));
        when(userRepository.findByEmployee_Id(9L)).thenReturn(Optional.of(user));

        PasswordSetupService.SetupCode setupCode = service.issueSetupCode(9L);

        assertEquals("employee-jane", setupCode.username());
        assertEquals(Role.ROLE_EMPLOYEE, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void refusesToResetOrChangePrivilegedAccountLinkedToEmployeeRecord() {
        Employee employee = Employee.builder().id(10L).status(EmployeeStatus.ACTIVE).build();
        User hr = User.builder().username("hr").role(Role.ROLE_HR).employee(employee).build();
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(employee));
        when(userRepository.findByEmployee_Id(10L)).thenReturn(Optional.of(hr));

        assertThrows(IllegalArgumentException.class, () -> service.issueSetupCode(10L));

        assertEquals(Role.ROLE_HR, hr.getRole());
        verify(userRepository, never()).save(any());
    }

    @Test
    void refusesSetupForInactiveEmployee() {
        Employee employee = Employee.builder().id(7L).status(EmployeeStatus.INACTIVE).build();
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(employee));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.issueSetupCode(7L));

        assertEquals("Restore this employee before setting up login access", error.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void refusesToClaimUsernameAlreadyOwnedByAnotherAccount() {
        Employee employee = Employee.builder().id(42L).status(EmployeeStatus.ACTIVE).build();
        when(employeeRepository.findById(42L)).thenReturn(Optional.of(employee));
        when(userRepository.findByEmployee_Id(42L)).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("42")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.issueSetupCode(42L));
        verify(userRepository, never()).save(any());
    }
}
