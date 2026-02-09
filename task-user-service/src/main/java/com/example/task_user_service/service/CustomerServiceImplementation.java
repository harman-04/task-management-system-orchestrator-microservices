// Package declaration → groups related classes together.
package com.example.task_user_service.service;

import com.example.task_user_service.repository.UserRepository; // Repository for accessing User collection in MongoDB.
import com.example.task_user_service.usermodel.User;           // User entity mapped to MongoDB.
import lombok.RequiredArgsConstructor;                         // Lombok → generates constructor for final fields.
import org.springframework.security.core.GrantedAuthority;     // Represents user roles/authorities.
import org.springframework.security.core.userdetails.UserDetails; // Core interface for user authentication details.
import org.springframework.security.core.userdetails.UserDetailsService; // Loads user-specific data for authentication.
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Exception if user not found.
import org.springframework.stereotype.Service;                 // Marks this class as a Spring-managed service bean.

import java.util.ArrayList;
import java.util.List;

// @Service → Marks this class as a service component.
// @RequiredArgsConstructor → Lombok generates constructor for final fields (dependency injection).
@Service
@RequiredArgsConstructor
public class CustomerServiceImplementation implements UserDetailsService {

    // ================================
    // Dependency Injection
    // ================================
    // Injects UserRepository to fetch user details from MongoDB.
    private final UserRepository userRepository;

    // ================================
    // Method: loadUserByUsername
    // ================================
    // Purpose:
    // - Required by Spring Security.
    // - Loads user details by username (here, email).
    // - Returns a UserDetails object used for authentication.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Step 1: Fetch user from MongoDB using email.
        User user = userRepository.findByEmail(username);
        System.out.println(user);

        // Step 2: If user not found → throw exception.
        if (user == null) {
            throw new UsernameNotFoundException("User Not found with email : " + username);
        }

        // Step 3: Log loaded user details for debugging.
        System.out.println("Loaded User : with email : " + username + " and Role is : " + user.getRole());

        // Step 4: Prepare authorities (roles).
        // Currently empty list → can be populated with user.getRole().
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Step 5: Return Spring Security User object.
        // Parameters:
        // - username (email)
        // - password (hashed with BCrypt)
        // - authorities (roles)
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}