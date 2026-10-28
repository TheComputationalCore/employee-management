package com.empmgmt.security.service;

import com.empmgmt.security.model.User;
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
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String issueSetupCode(Long employeeId) {
        User user = userRepository.findByEmployee_Id(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("No login account exists for this employee"));

        String setupCode = randomSecret();
        user.setPassword(passwordEncoder.encode(randomSecret()));
        user.setSetupTokenHash(hash(setupCode));
        user.setSetupTokenExpiresAt(LocalDateTime.now().plusHours(TOKEN_LIFETIME_HOURS));
        userRepository.save(user);
        return setupCode;
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
