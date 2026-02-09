// Package declaration → groups related classes together.
package com.example.task_service.taskModel;

import com.example.task_service.enums.TaskStatus;        // Enum for task status (e.g., PENDING, COMPLETED).
import com.fasterxml.jackson.annotation.JsonFormat;      // Used to format JSON output for fields like status.
import lombok.AllArgsConstructor;                       // Lombok → generates constructor with all fields.
import lombok.Data;                                     // Lombok → generates getters, setters, equals, hashCode, toString.
import lombok.NoArgsConstructor;                        // Lombok → generates default no-argument constructor.
import org.springframework.data.annotation.Id;          // Marks field as primary key in MongoDB.
import org.springframework.data.mongodb.core.mapping.Document; // Maps class to MongoDB collection.

import java.time.LocalDateTime;                         // Represents date/time fields.
import java.util.ArrayList;                             // Used for initializing tags list.
import java.util.List;                                  // Stores tags for tasks.

// @Data → Lombok generates boilerplate code (getters/setters, equals, hashCode, toString).
// @Document(collection = "Tasks") → Maps this class to MongoDB collection named "Tasks".
// @NoArgsConstructor → Default constructor.
// @AllArgsConstructor → Constructor with all fields.
@Data
@Document(collection = "Tasks")
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    // ================================
    // Primary Key
    // ================================
    @Id
    private String id; // Unique identifier for each task (MongoDB ObjectId).

    // ================================
    // Task Details
    // ================================
    private String title;          // Title of the task (short description).
    private String description;    // Detailed description of the task.
    private String imageUrl;       // Optional image URL related to the task.
    private String assignedUserId; // ID of the user assigned to this task.

    // ================================
    // Task Status
    // ================================
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private TaskStatus status;     // Enum representing task status (e.g., PENDING, IN_PROGRESS, COMPLETED).

    // ================================
    // Time Fields
    // ================================
    private LocalDateTime deadline;   // Deadline for task completion.
    private LocalDateTime createdAt;  // Timestamp when task was created.

    // ================================
    // Tags
    // ================================
    private List<String> tags = new ArrayList<>(); // Tags for categorizing tasks (e.g., "urgent", "backend").
}