// Package declaration → groups related classes together.
package com.example.task_submission_service.service;

import com.example.task_submission_service.submissionModel.TaskSubmission; // TaskSubmission entity mapped to MongoDB.

import java.util.List;

// SubmissionService → Interface that defines the contract for submission-related operations.
// Interfaces only declare methods (no implementation).
// The actual logic will be provided in a class that implements this interface (e.g., SubmissionServiceImpl).
public interface SubmissionService {

    // ================================
    // Submit Task
    // ================================
    // Purpose:
    // - Allows a user to submit a task solution.
    // - Requires taskId, GitHub link, userId, and JWT for authentication.
    // - Throws Exception if submission fails (e.g., invalid JWT, missing task).
    TaskSubmission submitTask(String taskId, String githubLink, String userId, String jwt) throws Exception;

    // ================================
    // Get Submission by ID
    // ================================
    // Purpose:
    // - Fetch a specific submission by its unique ID.
    // - Throws Exception if submission not found.
    TaskSubmission getTaskSubmissionById(String submissionId) throws Exception;

    // ================================
    // Get All Submissions
    // ================================
    // Purpose:
    // - Fetch all submissions across all tasks.
    // - Useful for admins to review all submissions.
    List<TaskSubmission> getAllTaskSubmissions();

    // ================================
    // Get Submissions by Task ID
    // ================================
    // Purpose:
    // - Fetch all submissions for a specific task.
    // - Useful for task owners/admins to review submissions for one task.
    List<TaskSubmission> getTaskSubmissionByTaskId(String taskId);

    // ================================
    // Accept or Decline Submission
    // ================================
    // Purpose:
    // - Allows admin/reviewer to accept or reject a submission.
    // - id → submission ID.
    // - status → new status ("ACCEPTED" or "REJECTED").
    // - Throws Exception if submission not found or invalid status provided.
    TaskSubmission acceptDeclineTaskSubmission(String id, String status) throws Exception;
}