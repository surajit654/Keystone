package com.KEYSTONE.config;

import com.KEYSTONE.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Permit all OPTIONS preflight requests globally
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Public endpoints
                        .requestMatchers("/api/auth/**", "/auth/**").permitAll()
                        .requestMatchers("/api/hello", "/hello").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Role-based protected endpoints
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow Vercel frontend, local development, and wildcard subdomains
        configuration.setAllowedOrigins(List.of(
                "https://keystone-virid-beta.vercel.app",
                "http://localhost:5173",
                "http://localhost:3000",
                "http://localhost:8080"
        ));
        configuration.setAllowedOriginPatterns(List.of(
                "https://*.vercel.app",
                "http://localhost:*",
                "https://*.up.railway.app"
        ));

        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}