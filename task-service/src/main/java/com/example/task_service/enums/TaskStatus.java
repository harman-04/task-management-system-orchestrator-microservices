// Package declaration → groups related classes together.
package com.example.task_service.enums;

import com.fasterxml.jackson.annotation.JsonFormat; // Ensures enum values are serialized as strings in JSON.

// @JsonFormat(shape = JsonFormat.Shape.STRING)
// Purpose:
// - When converting TaskStatus to JSON, values will be represented as strings (e.g., "PENDING").
// - Without this, enums might be serialized as numbers (0, 1, 2), which is less readable.
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TaskStatus {

    // ================================
    // Enum Constants
    // ================================
    PENDING("PENDING"),   // Task is created but not yet assigned.
    ASSIGNED("ASSIGNED"), // Task has been assigned to a user.
    DONE("DONE");         // Task has been completed.

    // ================================
    // Field
    // ================================
    private final String name; // Stores the string representation of the status.

    // ================================
    // Constructor
    // ================================
    // Each enum constant calls this constructor with its string name.
    TaskStatus(String name) {
        this.name = name;
    }

    // ================================
    // Getter
    // ================================
    // Returns the string name of the enum constant.
    // Example: TaskStatus.PENDING.getName() → "PENDING"
    public String getName() {
        return name;
    }
}