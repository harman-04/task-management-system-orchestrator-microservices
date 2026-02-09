// Package declaration → groups related classes together.
package com.example.task_submission_service.controller;

import com.example.task_submission_service.dto.UserDTO;              // DTO representing user details (from USER-SERVICE).
import com.example.task_submission_service.service.SubmissionService; // Service interface for submission operations.
import com.example.task_submission_service.service.UserServiceClient; // Feign client to call USER-SERVICE for user profile.
import com.example.task_submission_service.submissionModel.TaskSubmission; // TaskSubmission entity mapped to MongoDB.
import lombok.RequiredArgsConstructor;   // Lombok → generates constructor for final fields.
import org.springframework.http.HttpStatus;               // HTTP status codes.
import org.springframework.http.ResponseEntity;           // Represents HTTP responses.
import org.springframework.web.bind.annotation.*;         // REST controller + mapping annotations.

import java.util.List;

// @RestController → Marks this class as a REST controller (returns JSON responses).
// @RequestMapping("/api/submissions") → Base URL for submission-related endpoints.
// @RequiredArgsConstructor → Lombok generates constructor for final fields (dependency injection).
@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubController {

    // ================================
    // Dependencies (Injected via Constructor)
    // ================================
    private final SubmissionService submissionService;   // Provides submission-related operations.
    private final UserServiceClient userServiceClient;   // Feign client to call USER-SERVICE for user profile.

    // ================================
    // Submit Task (POST /api/submissions)
    // ================================
    @PostMapping
    public ResponseEntity<TaskSubmission> submitTask(@RequestParam String taskId,
                                                     @RequestParam String githubLink,
                                                     @RequestHeader("Authorization") String jwt) throws Exception {
        // Step 1: Fetch user profile from USER-SERVICE using JWT.
        UserDTO user = userServiceClient.getUserProfile(jwt);

        // Step 2: Submit task via SubmissionService.
        TaskSubmission submission = submissionService.submitTask(taskId, githubLink, user.id(), jwt);

        // Step 3: Return created submission with HTTP 201 Created.
        return new ResponseEntity<>(submission, HttpStatus.CREATED);
    }

    // ================================
    // Get All Submissions (GET /api/submissions)
    // ================================
    @GetMapping
    public ResponseEntity<List<TaskSubmission>> getAllSubmissions() {
        // Fetch all submissions.
        List<TaskSubmission> submissions = submissionService.getAllTaskSubmissions();
        return ResponseEntity.ok(submissions);
    }

    // ================================
    // Get Submission by ID (GET /api/submissions/{id})
    // ================================
    @GetMapping("/{id}")
    public ResponseEntity<TaskSubmission> getSubmissionById(@PathVariable String id) throws Exception {
        // Fetch submission by ID.
        return ResponseEntity.ok(submissionService.getTaskSubmissionById(id));
    }

    // ================================
    // Get Submissions by Task ID (GET /api/submissions/task/{taskId})
    // ================================
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskSubmission>> getSubmissionByTaskId(@PathVariable String taskId) throws Exception {
        // Fetch submissions for a specific task.
        return ResponseEntity.ok(submissionService.getTaskSubmissionByTaskId(taskId));
    }

    // ================================
    // Accept or Decline Submission (PUT /api/submissions/{id}?status=ACCEPTED/REJECTED)
    // ================================
    @PutMapping("/{id}")
    public ResponseEntity<TaskSubmission> acceptOrDeclineSubmission(@PathVariable String id,
                                                                    @RequestParam String status) throws Exception {
        // Update submission status (ACCEPTED or REJECTED).
        TaskSubmission submission = submissionService.acceptDeclineTaskSubmission(id, status);
        return ResponseEntity.ok(submission);
    }
}