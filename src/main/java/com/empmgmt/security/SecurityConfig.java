package com.empmgmt.security;

import com.empmgmt.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/do-login", "/account/setup", "/error", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/careers/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers(
                    "/web/attendance/my", "/web/attendance/clock-in", "/web/attendance/clock-out",
                    "/web/leave/apply", "/web/leave/my", "/web/leave/my/**",
                    "/web/payroll/my", "/web/performance/my", "/web/performance/self/**",
                    "/web/onboarding/my", "/web/onboarding/task/upload/**", "/web/onboarding/task/complete/**",
                    "/web/kpi/self/**"
                ).hasAnyRole("ADMIN", "HR", "EMPLOYEE")
                .requestMatchers("/web/**", "/api/v1/**").hasAnyRole("ADMIN", "HR")
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").hasAnyRole("ADMIN", "HR")
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/do-login")
                .defaultSuccessUrl("/web/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )

            .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
