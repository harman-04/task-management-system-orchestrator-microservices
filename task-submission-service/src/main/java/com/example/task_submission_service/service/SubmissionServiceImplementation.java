// Package declaration → groups related classes together.
package com.example.task_submission_service.service;

import com.example.task_submission_service.dto.TaskDTO;          // DTO representing task details (fetched from TASK-SERVICE).
import com.example.task_submission_service.enums.SubmissionStatus; // Enum for submission status (PENDING, ACCEPTED, REJECTED).
import com.example.task_submission_service.repository.SubRepository; // Repository for accessing TaskSubmission collection in MongoDB.
import com.example.task_submission_service.submissionModel.TaskSubmission; // TaskSubmission entity mapped to MongoDB.
import lombok.RequiredArgsConstructor;   // Lombok → generates constructor for final fields.
import org.springframework.stereotype.Service; // Marks this class as a Spring-managed service bean.

import java.time.LocalDateTime;          // Used for submission timestamps.
import java.util.List;

// @Service → Marks this class as a service component.
// @RequiredArgsConstructor → Lombok generates constructor for final fields (dependency injection).
@Service
@RequiredArgsConstructor
public class SubmissionServiceImplementation implements SubmissionService {

    // ================================
    // Dependencies (Injected via Constructor)
    // ================================
    private final SubRepository subRepository;     // Provides MongoDB access for submissions.
    private final TaskServiceClient taskServiceClient; // Feign client to call TASK-SERVICE for task details.

    // ================================
    // Submit Task
    // ================================
    @Override
    public TaskSubmission submitTask(String taskId, String githubLink, String userId, String jwt) throws Exception {
        // Step 1: Validate task existence by calling TASK-SERVICE via Feign client.
        TaskDTO task = taskServiceClient.getTaskById(taskId, jwt);

        if (task == null) {
            throw new Exception("Task not found with id: " + taskId);
        }

        // Step 2: Create new TaskSubmission object.
        TaskSubmission taskSubmission = new TaskSubmission();
        taskSubmission.setTaskId(taskId);
        taskSubmission.setGithubLink(githubLink);
        taskSubmission.setUserId(userId);
        taskSubmission.setSubmissionTime(LocalDateTime.now());
        taskSubmission.setStatus(SubmissionStatus.PENDING); // Default status.

        // Step 3: Save submission to MongoDB.
        return subRepository.save(taskSubmission);
    }

    // ================================
    // Get Submission by ID
    // ================================
    @Override
    public TaskSubmission getTaskSubmissionById(String submissionId) throws Exception {
        // Fetch submission by ID or throw exception if not found.
        return subRepository.findById(submissionId)
                .orElseThrow(() -> new Exception("Submission not found with id: " + submissionId));
    }

    // ================================
    // Get All Submissions
    // ================================
    @Override
    public List<TaskSubmission> getAllTaskSubmissions() {
        // Fetch all submissions from MongoDB.
        return subRepository.findAll();
    }

    // ================================
    // Get Submissions by Task ID
    // ================================
    @Override
    public List<TaskSubmission> getTaskSubmissionByTaskId(String taskId) {
        // Fetch all submissions and filter by taskId.
        return subRepository.findAll().stream()
                .filter(submission -> submission.getTaskId().equals(taskId))
                .toList();
    }

    // ================================
    // Accept or Decline Submission
    // ================================
    @Override
    public TaskSubmission acceptDeclineTaskSubmission(String id, String status) throws Exception {
        // Step 1: Fetch submission by ID.
        TaskSubmission submission = getTaskSubmissionById(id);

        // Step 2: Convert status string to enum (case-insensitive).
        SubmissionStatus newStatus = SubmissionStatus.valueOf(status.toUpperCase());
        submission.setStatus(newStatus);

        // Step 3: If submission is accepted, mark task as complete in TASK-SERVICE.
        if (newStatus == SubmissionStatus.ACCEPTED) {
            taskServiceClient.completeTask(submission.getTaskId());
        }

        // Step 4: Save updated submission.
        return subRepository.save(submission);
    }
}