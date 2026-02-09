// Package declaration → groups related classes together.
package com.example.task_user_service.response;

import lombok.AllArgsConstructor; // Lombok → generates constructor with all fields.
import lombok.Data;               // Lombok → generates getters, setters, equals, hashCode, toString.
import lombok.NoArgsConstructor;  // Lombok → generates default no-argument constructor.

// @AllArgsConstructor → Constructor with all fields (jwt, message, status).
// @NoArgsConstructor → Default constructor (no arguments).
// @Data → Lombok generates boilerplate code (getters/setters, equals, hashCode, toString).
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponse {

    // ================================
    // JWT Token
    // ================================
    // Stores the JWT token generated after successful authentication.
    // Example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    private String jwt;

    // ================================
    // Message
    // ================================
    // Stores a human-readable message about the authentication result.
    // Example: "Login successful" or "Invalid credentials".
    private String message;

    // ================================
    // Status
    // ================================
    // Boolean flag indicating success or failure of authentication.
    // true → authentication succeeded.
    // false → authentication failed.
    private Boolean status;
}