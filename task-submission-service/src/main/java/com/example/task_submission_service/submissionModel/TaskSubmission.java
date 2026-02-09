// Package declaration → groups related classes together.
package com.example.task_submission_service.submissionModel;

import com.example.task_submission_service.enums.SubmissionStatus; // Enum for submission status (PENDING, APPROVED, REJECTED).
import lombok.AllArgsConstructor;   // Lombok → generates constructor with all fields.
import lombok.Data;                 // Lombok → generates getters, setters, equals, hashCode, toString.
import lombok.NoArgsConstructor;    // Lombok → generates default no-argument constructor.
import org.springframework.data.annotation.Id;          // Marks field as primary key in MongoDB.
import org.springframework.data.mongodb.core.mapping.Document; // Maps class to MongoDB collection.

import java.time.LocalDateTime;     // Represents date/time fields.

// @Document(collection = "taskSubmission") → Maps this class to MongoDB collection named "taskSubmission".
// @Data → Lombok generates boilerplate code (getters/setters, equals, hashCode, toString).
// @AllArgsConstructor → Constructor with all fields.
// @NoArgsConstructor → Default no-argument constructor.
@Document(collection = "taskSubmission")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskSubmission {

    // ================================
    // Primary Key
    // ================================
    @Id
    private String id; // Unique identifier for each submission (MongoDB ObjectId).

    // ================================
    // Task Reference
    // ================================
    private String taskId; // ID of the task being submitted.

    // ================================
    // Submission Details
    // ================================
    private String githubLink; // Link to GitHub repository containing task solution.

    // ================================
    // Submission Status
    // ================================
    private SubmissionStatus status = SubmissionStatus.PENDING;
    // Default status is PENDING until reviewed (can later be APPROVED or REJECTED).

    // ================================
    // User Reference
    // ================================
    private String userId; // ID of the user who submitted the task.

    // ================================
    // Time Fields
    // ================================
    private LocalDateTime submissionTime; // Timestamp when submission was made.
}