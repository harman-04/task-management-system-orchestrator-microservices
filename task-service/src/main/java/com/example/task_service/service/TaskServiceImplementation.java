// Package declaration → groups related classes together.
package com.example.task_service.service;

import com.example.task_service.enums.TaskStatus;        // Enum for task status (PENDING, ASSIGNED, DONE).
import com.example.task_service.repository.TaskRepository; // Repository for accessing Task collection in MongoDB.
import com.example.task_service.taskModel.Task;         // Task entity mapped to MongoDB.
import lombok.RequiredArgsConstructor;                  // Lombok → generates constructor for final fields.
import org.springframework.stereotype.Service;          // Marks this class as a Spring-managed service bean.

import java.time.LocalDateTime;                         // Used for timestamps (createdAt, deadline).
import java.util.Comparator;                            // Used for sorting tasks.
import java.util.List;
import java.util.stream.Collectors;                     // Used for filtering and sorting with streams.

// @Service → Marks this class as a service component.
// @RequiredArgsConstructor → Lombok generates constructor for final fields (dependency injection).
@Service
@RequiredArgsConstructor
public class TaskServiceImplementation implements TaskService {

    // ================================
    // Dependency Injection
    // ================================
    private final TaskRepository taskRepository; // Provides MongoDB access for tasks.

    // ================================
    // Create Task
    // ================================
    @Override
    public Task create(Task task, String requestRole) throws Exception {
        // Only admins can create tasks.
        if (!requestRole.equals("ROLE_ADMIN")) {
            throw new Exception("Only Admins can create tasks");
        }

        // Set default status and creation timestamp.
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());

        // Save task to MongoDB.
        return taskRepository.save(task);
    }

    // ================================
    // Get Task by ID
    // ================================
    @Override
    public Task getTaskById(String id) throws Exception {
        // Fetch task by ID, return null if not found.
        return taskRepository.findById(id).orElse(null);
    }

    // ================================
    // Get All Tasks (with optional filtering)
    // ================================
    @Override
    public List<Task> getAllTasks(TaskStatus taskStatus, String sortByDeadline, String sortByCreatedAt) {
        // Fetch all tasks.
        List<Task> allTasks = taskRepository.findAll();

        // Filter by status if provided.
        List<Task> filteredTasks = allTasks.stream()
                .filter(task -> taskStatus == null || task.getStatus().name().equalsIgnoreCase(taskStatus.toString()))
                .toList();

        return filteredTasks;
    }

    // ================================
    // Update Task
    // ================================
    @Override
    public Task updateTask(String id, Task updateTask, String userId) throws Exception {
        // Fetch existing task.
        Task existingTasks = getTaskById(id);

        // Update only non-null fields.
        if (updateTask.getTitle() != null) {
            existingTasks.setTitle(updateTask.getTitle());
        }
        if (updateTask.getImageUrl() != null) {
            existingTasks.setImageUrl(updateTask.getImageUrl());
        }
        if (updateTask.getDescription() != null) {
            existingTasks.setDescription(updateTask.getDescription());
        }
        if (updateTask.getStatus() != null) {
            existingTasks.setStatus(updateTask.getStatus());
        }
        if (updateTask.getDeadline() != null) {
            existingTasks.setDeadline(updateTask.getDeadline());
        }

        // Save updated task.
        return taskRepository.save(existingTasks);
    }

    // ================================
    // Delete Task
    // ================================
    @Override
    public void deleteTask(String id) throws Exception {
        // Ensure task exists before deleting.
        getTaskById(id);
        taskRepository.deleteById(id);
    }

    // ================================
    // Assign Task to User
    // ================================
    @Override
    public Task assignedToUser(String id, String userId) throws Exception {
        // Fetch task by ID.
        Task task = getTaskById(id);

        // Assign to user and update status.
        task.setAssignedUserId(userId);
        task.setStatus(TaskStatus.ASSIGNED);

        return taskRepository.save(task);
    }

    // ================================
    // Get Tasks Assigned to User (basic filter)
    // ================================
    public List<Task> assignedUsersTask(String userId, TaskStatus taskStatus) {
        // Fetch tasks assigned to user.
        List<Task> allTasks = taskRepository.findByAssignedUserId(userId);

        // Filter by status if provided.
        return allTasks.stream()
                .filter(task -> taskStatus == null || task.getStatus() == taskStatus)
                .toList();
    }

    // ================================
    // Get Tasks Assigned to User (with sorting)
    // ================================
    @Override
    public List<Task> assignedUsersTask(String userId, TaskStatus taskStatus, String sortByDeadline, String sortByCreatedAt) {
        // Fetch tasks assigned to user.
        List<Task> allTasks = taskRepository.findByAssignedUserId(userId);

        // Filter by status if provided.
        List<Task> filteredTasks = allTasks.stream()
                .filter(task -> taskStatus == null || task.getStatus() == taskStatus)
                .collect(Collectors.toList());

        // Sort by deadline or createdAt if requested.
        if (sortByDeadline != null && !sortByDeadline.isEmpty()) {
            filteredTasks.sort(Comparator.comparing(Task::getDeadline, Comparator.nullsLast(Comparator.naturalOrder())));
        } else if (sortByCreatedAt != null && !sortByCreatedAt.isEmpty()) {
            filteredTasks.sort(Comparator.comparing(Task::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())));
        }

        return filteredTasks;
    }

    // ================================
    // Complete Task
    // ================================
    @Override
    public Task completeTask(String taskId) throws Exception {
        // Fetch task by ID.
        Task task = getTaskById(taskId);

        // Mark as DONE.
        task.setStatus(TaskStatus.DONE);

        return taskRepository.save(task);
    }
}