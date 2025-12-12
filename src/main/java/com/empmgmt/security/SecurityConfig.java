package com.empmgmt.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        // REGISTER AUTH PROVIDER HERE (new recommended way)
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

                /* RULES */
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**",
                                "/plugins/**", "/webjars/**").permitAll()

                        .requestMatchers("/login", "/do-login", "/error").permitAll()
                        .requestMatchers("/careers/**").permitAll()

                        .requestMatchers("/web/dashboard").authenticated()

                        .requestMatchers("/web/employees/**").hasAnyRole("ADMIN", "HR")
                        .requestMatchers("/web/departments/**").hasAnyRole("ADMIN", "HR")

                        .requestMatchers("/web/attendance/my",
                                "/web/attendance/clock-in",
                                "/web/attendance/clock-out").hasRole("EMPLOYEE")

                        .requestMatchers("/web/attendance", "/web/attendance/**")
                                .hasAnyRole("ADMIN", "HR")

                        .requestMatchers(HttpMethod.GET,
                                "/web/leave/apply",
                                "/web/leave/my",
                                "/web/leave/my/**").hasRole("EMPLOYEE")

                        .requestMatchers(HttpMethod.POST, "/web/leave/apply")
                                .hasRole("EMPLOYEE")

                        .requestMatchers("/web/leave",
                                "/web/leave/calendar",
                                "/web/leave/approve/**",
                                "/web/leave/reject/**")
                                .hasAnyRole("ADMIN", "HR")

                        .requestMatchers("/web/payroll/my").hasRole("EMPLOYEE")
                        .requestMatchers("/web/payroll/**").hasAnyRole("ADMIN", "HR")

                        .requestMatchers("/web/performance/my",
                                "/web/performance/my/**",
                                "/web/performance/self/**").hasRole("EMPLOYEE")

                        .requestMatchers("/web/performance",
                                "/web/performance/",
                                "/web/performance/create",
                                "/web/performance/create/**",
                                "/web/performance/manager/**")
                                .hasAnyRole("ADMIN", "HR")

                        .requestMatchers("/web/onboarding/**")
                                .hasAnyRole("ADMIN", "HR")

                        .requestMatchers("/web/recruitment/**")
                                .hasAnyRole("ADMIN", "HR")

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

    // OPTIONAL: But required for some flows
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
