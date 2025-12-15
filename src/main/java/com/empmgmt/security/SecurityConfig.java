package com.empmgmt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        AuthenticationManager authenticationManager = authBuilder.build();

        http
            .authenticationManager(authenticationManager)

            /* CSRF */
            .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )

            /* AUTHORIZATION RULES */
            .authorizeHttpRequests(auth -> auth

                /* STATIC */
                .requestMatchers(
                        "/css/**", "/js/**", "/images/**",
                        "/plugins/**", "/webjars/**"
                ).permitAll()

                /* PUBLIC */
                .requestMatchers("/login", "/do-login", "/error").permitAll()
                .requestMatchers("/careers/**").permitAll()

                /* DASHBOARD */
                .requestMatchers("/web/dashboard").authenticated()

                /* EMPLOYEES & DEPARTMENTS */
                .requestMatchers("/web/employees/**").hasAnyRole("ADMIN", "HR")
                .requestMatchers("/web/departments/**").hasAnyRole("ADMIN", "HR")

                /* ATTENDANCE */
                .requestMatchers(
                        "/web/attendance/my",
                        "/web/attendance/clock-in",
                        "/web/attendance/clock-out"
                ).hasRole("EMPLOYEE")

                .requestMatchers("/web/attendance/**")
                        .hasAnyRole("ADMIN", "HR")

                /* LEAVE */
                .requestMatchers(HttpMethod.GET,
                        "/web/leave/my/**",
                        "/web/leave/apply"
                ).hasRole("EMPLOYEE")

                .requestMatchers(HttpMethod.POST,
                        "/web/leave/apply"
                ).hasRole("EMPLOYEE")

                .requestMatchers("/web/leave/**")
                        .hasAnyRole("ADMIN", "HR")

                /* PAYROLL */
                .requestMatchers("/web/payroll/my").hasRole("EMPLOYEE")
                .requestMatchers("/web/payroll/**").hasAnyRole("ADMIN", "HR")

                /* PERFORMANCE */
                .requestMatchers("/web/performance/my/**")
                        .hasRole("EMPLOYEE")

                .requestMatchers("/web/performance/**")
                        .hasAnyRole("ADMIN", "HR")

                /* ONBOARDING & RECRUITMENT */
                .requestMatchers("/web/onboarding/**").hasAnyRole("ADMIN", "HR")
                .requestMatchers("/web/recruitment/**").hasAnyRole("ADMIN", "HR")

                .anyRequest().authenticated()
            )

            /* LOGIN */
            .formLogin(form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/do-login")
                    .defaultSuccessUrl("/web/dashboard", true)
                    .failureUrl("/login?error=true")
                    .permitAll()
            )

            /* LOGOUT */
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=true")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .clearAuthentication(true)
                    .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
