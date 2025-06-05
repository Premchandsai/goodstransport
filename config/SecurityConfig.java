package com.p2p.transport.config;

import com.p2p.transport.security.JwtAuthenticationFilter;
import com.p2p.transport.response.ApiResponse;
import com.p2p.transport.response.ErrorDetail;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Role-based access for specific endpoints
                        .requestMatchers("/api/rides/**").hasAnyRole("DRIVER", "ADMIN")
                        .requestMatchers("/api/requests/**", "/api/payments/**", "/api/ratings/**", "/api/tracking/**").hasAnyRole("SENDER", "DRIVER", "ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Catch-all: all other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler())
                );
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            ApiResponse<Object> apiResponse = new ApiResponse<>(
                    403,
                    "Access denied",
                    Collections.singletonList(new ErrorDetail("ACCESS_DENIED", "You do not have permission to access this resource"))
            );
            new ObjectMapper().writeValue(response.getWriter(), apiResponse);
        };
    }
}