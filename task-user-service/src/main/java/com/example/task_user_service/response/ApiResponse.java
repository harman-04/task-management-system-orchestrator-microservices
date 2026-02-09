// Package declaration → groups related classes together.
package com.example.task_user_service.response;

import lombok.AllArgsConstructor; // Lombok → generates constructor with all fields.
import lombok.Data;               // Lombok → generates getters, setters, equals, hashCode, toString.
import lombok.NoArgsConstructor;  // Lombok → generates default no-argument constructor.

// @Data → Lombok generates boilerplate code (getters/setters, equals, hashCode, toString).
// @AllArgsConstructor → Constructor with all fields (message, status).
// @NoArgsConstructor → Default constructor (no arguments).
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    // ================================
    // Message
    // ================================
    // Stores a human-readable message about the API response.
    // Example: "User created successfully" or "Error: User not found".
    private String message;

    // ================================
    // Status
    // ================================
    // Boolean flag indicating success or failure of the API call.
    // true → success, false → failure.
    private Boolean status;
}