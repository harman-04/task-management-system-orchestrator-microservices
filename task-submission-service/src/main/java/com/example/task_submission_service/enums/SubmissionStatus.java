// Package declaration → groups related classes together.
package com.example.task_submission_service.enums;

// SubmissionStatus → Enum representing the possible states of a task submission.
// Purpose:
// - Provides a fixed set of constants (PENDING, ACCEPTED, REJECTED).
// - Used in TaskSubmission model to track the review status of a submission.
public enum SubmissionStatus {

    // ================================
    // Enum Constants
    // ================================
    PENDING,   // Submission has been made but not yet reviewed.
    ACCEPTED,  // Submission has been reviewed and approved.
    REJECTED   // Submission has been reviewed and rejected.
}