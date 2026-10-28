package com.empmgmt.security.service;

import com.empmgmt.security.model.User;
import com.empmgmt.model.Employee;
import com.empmgmt.model.EmployeeStatus;
import com.empmgmt.model.Role;
import com.empmgmt.repository.EmployeeRepository;
import com.empmgmt.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class PasswordSetupService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;
    private static final int TOKEN_LIFETIME_HOURS = 24;

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public record SetupCode(String code, String username) {}

    @Transactional
    public SetupCode issueSetupCode(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new IllegalArgumentException("Restore this employee before setting up login access");
        }

        User user = userRepository.findByEmployee_Id(employeeId).orElseGet(() -> {
            String username = employeeId.toString();
            if (userRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("The employee ID is already used by another login account");
            }
            return User.builder()
                    .username(username)
                    .role(Role.ROLE_EMPLOYEE)
                    .employee(employee)
                    .build();
        });
        if (user.getRole() == Role.EMPLOYEE) {
            user.setRole(Role.ROLE_EMPLOYEE);
        } else if (user.getRole() != Role.ROLE_EMPLOYEE) {
            throw new IllegalArgumentException("This employee record is linked to a non-employee login account");
        }

        String setupCode = randomSecret();
        user.setPassword(passwordEncoder.encode(randomSecret()));
        user.setSetupTokenHash(hash(setupCode));
        user.setSetupTokenExpiresAt(LocalDateTime.now().plusHours(TOKEN_LIFETIME_HOURS));
        userRepository.save(user);
        return new SetupCode(setupCode, user.getUsername());
    }

    @Transactional
    public void completeSetup(String username, String setupCode, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(PasswordSetupService::invalidCode);

        if (setupCode == null || user.getSetupTokenHash() == null || user.getSetupTokenExpiresAt() == null
                || !LocalDateTime.now().isBefore(user.getSetupTokenExpiresAt())
                || !MessageDigest.isEqual(
                        user.getSetupTokenHash().getBytes(StandardCharsets.US_ASCII),
                        hash(setupCode).getBytes(StandardCharsets.US_ASCII))) {
            throw invalidCode();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setSetupTokenHash(null);
        user.setSetupTokenExpiresAt(null);
        userRepository.save(user);
    }

    public static String randomSecret() {
        byte[] bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }

    private static IllegalArgumentException invalidCode() {
        return new IllegalArgumentException("Setup code is invalid or expired");
    }
}
