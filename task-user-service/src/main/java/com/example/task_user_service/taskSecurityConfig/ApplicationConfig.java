// Package declaration → groups related classes together.
package com.example.task_user_service.taskSecurityConfig;

import org.springframework.context.annotation.Bean;                       // Marks a method as a Spring bean provider.
import org.springframework.context.annotation.Configuration;            // Marks this class as a Spring configuration class.
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Used to configure HTTP security.
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Enables Spring Security for web apps.
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Allows disabling CSRF easily.
import org.springframework.security.config.http.SessionCreationPolicy;  // Defines session management policy.
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Password encoder using BCrypt hashing.
import org.springframework.security.crypto.password.PasswordEncoder;    // Interface for password encoding.
import org.springframework.security.web.SecurityFilterChain;            // Defines the security filter chain.
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter; // Base filter for authentication.
import org.springframework.web.cors.CorsConfiguration;                  // Defines CORS rules.
import org.springframework.web.cors.CorsConfigurationSource;            // Provides CORS configuration source.

import java.util.Collections;
import java.util.List;

// @Configuration → Marks this class as a configuration provider.
// @EnableWebSecurity → Enables Spring Security for the application.
@Configuration
@EnableWebSecurity
public class ApplicationConfig {

    // ================================
    // Security Filter Chain
    // ================================
    // Defines how HTTP requests are secured in the application.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Session Management → Stateless (no server-side sessions).
                .sessionManagement(management ->
                        management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization Rules
                .authorizeHttpRequests(auth -> auth
                        // Allow all requests to /auth/** (login, register, etc.).
                        .requestMatchers("/auth/**").permitAll()
                        // Require authentication for /api/** endpoints.
                        .requestMatchers("/api/**").authenticated()
                        // Allow all other requests.
                        .anyRequest().permitAll())

                // Add custom JWT validation filter before BasicAuthenticationFilter.
                .addFilterBefore(new JwtTokenValidator(), BasicAuthenticationFilter.class)

                // Disable CSRF (not needed for stateless REST APIs).
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS with custom configuration.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Build the security filter chain.
                .build();
    }

    // ================================
    // CORS Configuration
    // ================================
    // Defines which origins, methods, and headers are allowed.
    private CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration ccfg = new CorsConfiguration();

            // Allow requests from frontend running on localhost:3000.
            ccfg.setAllowedOrigins(List.of("http://localhost:3000"));

            // Allow all HTTP methods (GET, POST, PUT, DELETE, etc.).
            ccfg.setAllowedMethods(Collections.singletonList("*"));

            // Allow credentials (cookies, authorization headers).
            ccfg.setAllowCredentials(true);

            // Allow all headers.
            ccfg.setAllowedHeaders(Collections.singletonList("*"));

            // Explicitly allow Authorization header (for JWT tokens).
            ccfg.setAllowedHeaders(List.of("Authorization"));

            // Cache CORS configuration for 3300 seconds.
            ccfg.setMaxAge(3300L);

            return ccfg;
        };
    }

    // ================================
    // Password Encoder Bean
    // ================================
    // Provides a BCryptPasswordEncoder for hashing user passwords.
    // Ensures passwords are stored securely in the database.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}