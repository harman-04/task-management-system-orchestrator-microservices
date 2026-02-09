// Package declaration → groups related classes together.
package com.example.task_user_service.taskSecurityConfig;

import io.jsonwebtoken.Claims;                  // Represents the payload (claims) inside JWT.
import io.jsonwebtoken.Jwts;                    // Utility class for building and parsing JWT tokens.
import io.jsonwebtoken.security.Keys;           // Utility for generating secure keys.
import org.springframework.security.core.Authentication; // Represents the authenticated user.
import org.springframework.security.core.GrantedAuthority; // Represents user roles/authorities.

import javax.crypto.SecretKey;                  // Secret key used for signing JWT.
import java.nio.charset.StandardCharsets;       // Charset for encoding secret key.
import java.util.Collection;                    // Collection interface for authorities.
import java.util.Date;                          // Used for issuedAt and expiration timestamps.
import java.util.HashSet;                       // Used to store unique authorities.
import java.util.Set;                           // Represents a set of authorities.

// JwtProvider → Utility class for generating and validating JWT tokens.
public class JwtProvider {

    // ================================
    // Secret Key
    // ================================
    // Generate a secure HMAC key using the SECRET_KEY defined in JwtConstant.
    private static final SecretKey key =
            Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    // ================================
    // Generate JWT Token
    // ================================
    // Purpose:
    // - Create a JWT token for an authenticated user.
    // - Include claims like email and authorities.
    // - Sign the token with SECRET_KEY.
    public static String generateToken(Authentication authentication) {
        // Extract roles/authorities from Authentication object.
        String roles = populateAuthorities(authentication.getAuthorities());

        // Build JWT token.
        return Jwts.builder()
                .issuedAt(new Date()) // Token issue time.
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // Expiration = 24 hours.
                .subject(authentication.getName()) // Subject = username/email.
                .claim("email", authentication.getName()) // Custom claim: email.
                .claim("authorities", roles) // Custom claim: roles.
                .signWith(key) // Sign with secret key.
                .compact(); // Build final token string.
    }

    // ================================
    // Populate Authorities
    // ================================
    // Converts GrantedAuthority collection into a comma-separated string.
    private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auth = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            auth.add(authority.getAuthority());
        }
        return String.join(",", auth); // Example: "ROLE_USER,ROLE_ADMIN"
    }

    // ================================
    // Extract Email from JWT Token
    // ================================
    // Purpose:
    // - Parse JWT token.
    // - Extract "email" claim.
    public static String getEmailFromJwtToken(String jwt) {
        // Remove "Bearer " prefix if present.
        if (jwt != null && jwt.startsWith("Bearer")) {
            jwt = jwt.substring(7);
        }
        try {
            // Parse JWT and extract claims.
            Claims claims = Jwts.parser()
                    .verifyWith(key) // Verify signature with secret key.
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();

            // Return email claim.
            return String.valueOf(claims.get("email"));
        } catch (Exception e) {
            System.out.println("Error extracting the email from the jwt token :" + e.getMessage());
            return null;
        }
    }
}