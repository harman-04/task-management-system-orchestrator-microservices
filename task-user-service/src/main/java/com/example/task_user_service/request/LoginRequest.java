// Package declaration → groups related classes together.
package com.example.task_user_service.request;

import lombok.AllArgsConstructor; // Lombok → generates constructor with all fields.
import lombok.Data;               // Lombok → generates getters, setters, equals, hashCode, toString.
import lombok.NoArgsConstructor;  // Lombok → generates default no-argument constructor.
import org.springframework.data.mongodb.core.mapping.Document; // Maps class to MongoDB collection (optional here).

// @Document → Marks this class as a MongoDB document.
// In this case, it's not strictly necessary since LoginRequest is usually just a DTO (not stored in DB).
// But if you wanted to persist login attempts, this annotation would map it to a MongoDB collection.
// @Data → Lombok generates boilerplate code (getters/setters, equals, hashCode, toString).
// @NoArgsConstructor → Default constructor.
// @AllArgsConstructor → Constructor with all fields.
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    // ================================
    // Email
    // ================================
    // Stores the user's email address.
    // Used as the username for authentication.
    private String email;

    // ================================
    // Password
    // ================================
    // Stores the user's password.
    // Typically compared against the hashed password in the database.
    private String password;

    // ================================
    // Setter Alias
    // ================================
    // Provides a method setUsername() that actually sets the email field.
    // This is useful because Spring Security often refers to "username",
    // but in this application, the username is actually the email.
    public void setUsername(String email) {
        this.email = email;
    }
}