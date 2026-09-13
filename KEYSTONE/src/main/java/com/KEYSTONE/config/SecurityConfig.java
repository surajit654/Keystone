package com.KEYSTONE.config;

import com.KEYSTONE.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/hello").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        .requestMatchers("/api/dispatcher/**")
                        .hasRole("DISPATCHER")

                        .requestMatchers("/api/technician/**")
                        .hasRole("TECHNICIAN")

                        .requestMatchers("/api/manager/**")
                        .hasRole("MANAGER")

                        .requestMatchers("/api/customers/**")
                        .hasAnyRole("DISPATCHER", "MANAGER", "CUSTOMER")

                        .requestMatchers("/api/parts/**")
                        .hasAnyRole("DISPATCHER", "MANAGER", "TECHNICIAN")

                        .requestMatchers("/api/reports/**")
                        .hasAnyRole("DISPATCHER", "MANAGER")

                        .requestMatchers("/api/work-orders/**")
                        .hasAnyRole("DISPATCHER", "MANAGER", "TECHNICIAN", "CUSTOMER")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}