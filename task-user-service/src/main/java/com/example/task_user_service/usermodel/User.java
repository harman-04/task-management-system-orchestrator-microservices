// Package declaration → groups related classes together.
package com.example.task_user_service.usermodel;

import com.fasterxml.jackson.annotation.JsonProperty; // Controls JSON serialization/deserialization (e.g., hide password in responses).
import lombok.AllArgsConstructor;                   // Lombok → generates constructor with all fields.
import lombok.Builder;                              // Lombok → generates builder pattern for object creation.
import lombok.Data;                                 // Lombok → generates getters, setters, equals, hashCode, toString.
import lombok.NoArgsConstructor;                    // Lombok → generates default no-argument constructor.
import org.springframework.data.annotation.Id;      // Marks field as primary key in MongoDB.
import org.springframework.data.mongodb.core.mapping.Document; // Maps class to MongoDB collection.

import java.util.List; // Used for storing completed task IDs as a list.

// @Document(collection = "user") → Maps this class to MongoDB collection named "user".
// Each instance of User will be stored as a document in this collection.
// @Data → Lombok generates boilerplate code (getters/setters, equals, hashCode, toString).
// @NoArgsConstructor → Default constructor.
// @AllArgsConstructor → Constructor with all fields.
// @Builder → Enables builder pattern for creating User objects easily.
@Document(collection = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    // ================================
    // Unique Identifier
    // ================================
    // @Id → Marks this field as the primary key in MongoDB.
    // MongoDB will automatically generate an ObjectId if not provided.
    @Id
    private String id;

    // ================================
    // Full Name
    // ================================
    // Stores the user's full name.
    private String fullName;

    // ================================
    // Email
    // ================================
    // Stores the user's email address.
    // Typically used for login and communication.
    private String email;

    // ================================
    // Password
    // ================================
    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    // → Ensures password is only accepted in requests (write-only).
    // → It will NOT be included in JSON responses (security best practice).
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // ================================
    // Role
    // ================================
    // Defines the user's role in the system.
    // Default value = "ROLE_CUSTOMER".
    // Can be changed to "ROLE_ADMIN" or other roles if needed.
    private String role = "ROLE_CUSTOMER";

    // ================================
    // Mobile Number
    // ================================
    // Stores the user's mobile phone number.
    private String mobile;

    // ================================
    // Completed Tasks
    // ================================
    // Stores IDs of tasks the user has completed.
    // Each task ID is represented as a Long.
    private List<Long> completedTasks;
}