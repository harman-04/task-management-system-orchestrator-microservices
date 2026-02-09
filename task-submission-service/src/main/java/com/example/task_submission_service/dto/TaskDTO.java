// Package declaration → groups related classes together.
package com.example.task_submission_service.dto;

// TaskDTO → A Java record used as a Data Transfer Object (DTO).
// Purpose:
// - Represents task details in a simplified form.
// - Used when communicating between microservices (e.g., via Feign Client).
// - Prevents exposing internal entity details directly.
// - Immutable → once created, values cannot be changed.
public record TaskDTO(
        String id,             // Unique identifier of the task (MongoDB ObjectId).
        String title,          // Title or name of the task.
        String status,         // Current status of the task (e.g., PENDING, ASSIGNED, DONE).
        String imageUrl,       // Optional image URL associated with the task.
        String deadline,       // Deadline for task completion (stored as String for easy transfer).
        String createdAt,      // Timestamp when task was created (stored as String for easy transfer).
        String assignedUserId  // ID of the user assigned to this task.
) { }