package com.empmgmt.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.http.HttpMethod;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                /* CSRF TOKEN IN COOKIE */
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )

                .authorizeHttpRequests(auth -> auth

                        /* STATIC FILES */
                        .requestMatchers("/css/**", "/js/**", "/images/**",
                                "/plugins/**", "/webjars/**").permitAll()

                        /* LOGIN */
                        .requestMatchers("/login", "/do-login", "/error").permitAll()

                        /* PUBLIC PAGES */
                        .requestMatchers("/careers/**").permitAll()

                        /* DASHBOARD */
                        .requestMatchers("/web/dashboard").authenticated()

                        /* EMPLOYEES */
                        .requestMatchers("/web/employees/**").hasAnyRole("ADMIN", "HR")

                        /* DEPARTMENTS */
                        .requestMatchers("/web/departments/**").hasAnyRole("ADMIN", "HR")

                        /* ========================== */
                        /*       ATTENDANCE          */
                        /* ========================== */

                        /* EMPLOYEE self-attendance */
                        .requestMatchers(
                                "/web/attendance/my",
                                "/web/attendance/clock-in",
                                "/web/attendance/clock-out"
                        ).hasRole("EMPLOYEE")

                        /* ADMIN/HR attendance mgmt */
                        .requestMatchers(
                                "/web/attendance",
                                "/web/attendance/**"
                        ).hasAnyRole("ADMIN", "HR")

                        /* ========================== */
                        /*           LEAVES           */
                        /* ========================== */

                        /* EMPLOYEE — leave pages */
                        .requestMatchers(HttpMethod.GET,
                                "/web/leave/apply",
                                "/web/leave/my",
                                "/web/leave/my/**"
                        ).hasRole("EMPLOYEE")

                        /* EMPLOYEE — submit leave */
                        .requestMatchers(HttpMethod.POST,
                                "/web/leave/apply"
                        ).hasRole("EMPLOYEE")

                        /* ADMIN/HR — leave mgmt */
                        .requestMatchers(
                                "/web/leave",
                                "/web/leave/calendar",
                                "/web/leave/approve/**",
                                "/web/leave/reject/**"
                        ).hasAnyRole("ADMIN", "HR")

                        /* ========================== */
                        /*           PAYROLL          */
                        /* ========================== */

                        /* EMPLOYEE — view own payroll */
                        .requestMatchers("/web/payroll/my").hasRole("EMPLOYEE")

                        /* ADMIN/HR — payroll mgmt */
                        .requestMatchers("/web/payroll/**").hasAnyRole("ADMIN", "HR")

                        /* ========================== */
                        /*    PERFORMANCE / KPI       */
                        /* ========================== */

/*         PERFORMANCE            */
/* ============================== */

/* EMPLOYEE — self review + my reviews */
                                .requestMatchers(
                                        "/web/performance/my",
                                        "/web/performance/my/**",
                                        "/web/performance/self/**"
                                ).hasRole("EMPLOYEE")

/* ADMIN/HR — manage performance */
                                .requestMatchers(
                                        "/web/performance",
                                        "/web/performance/",
                                        "/web/performance/create",
                                        "/web/performance/create/**",
                                        "/web/performance/manager/**"
                                ).hasAnyRole("ADMIN", "HR")



                                /* ONBOARDING */
                        .requestMatchers("/web/onboarding/**")
                        .hasAnyRole("ADMIN", "HR")

                        /* RECRUITMENT */
                        .requestMatchers("/web/recruitment/**")
                        .hasAnyRole("ADMIN", "HR")

                        /* EVERYTHING ELSE */
                        .anyRequest().authenticated()
                )

                /* LOGIN FORM */
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
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
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
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
