// Package declaration → groups related classes together.
package com.example.task_user_service.taskSecurityConfig;

import io.jsonwebtoken.Claims;                        // Represents the payload (claims) inside JWT.
import io.jsonwebtoken.Jwts;                          // Utility class for parsing JWT tokens.
import io.jsonwebtoken.security.Keys;                 // Utility for generating secure keys.
import jakarta.servlet.FilterChain;                   // Represents the chain of filters in a request.
import jakarta.servlet.ServletException;              // Exception for servlet errors.
import jakarta.servlet.http.HttpServletRequest;       // Represents incoming HTTP request.
import jakarta.servlet.http.HttpServletResponse;      // Represents outgoing HTTP response.
import org.springframework.security.authentication.BadCredentialsException; // Exception for invalid credentials.
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Authentication object.
import org.springframework.security.core.Authentication; // Represents authenticated user.
import org.springframework.security.core.GrantedAuthority; // Represents user roles/authorities.
import org.springframework.security.core.authority.AuthorityUtils; // Utility for converting roles string → authorities.
import org.springframework.security.core.context.SecurityContextHolder; // Holds authentication info for current request.
import org.springframework.web.filter.OncePerRequestFilter; // Ensures filter runs once per request.

import javax.crypto.SecretKey;                        // Secret key used for signing JWT.
import java.io.IOException;                           // Exception for I/O errors.
import java.nio.charset.StandardCharsets;             // Charset for encoding secret key.
import java.util.List;                                // Used for storing authorities list.

// JwtTokenValidator → Custom filter that validates JWT tokens on every request.
// Extends OncePerRequestFilter → ensures this filter runs once per request.
public class JwtTokenValidator extends OncePerRequestFilter {

    // ================================
    // Core Filter Logic
    // ================================
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Extract JWT token from Authorization header.
        String jwt = request.getHeader(JwtConstant.JWT_HEADER);

        // Step 2: Check if header exists and starts with "Bearer ".
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7); // Remove "Bearer " prefix.

            try {
                // Step 3: Create secret key from JwtConstant.
                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8));

                // Step 4: Parse JWT and extract claims.
                Claims claims = Jwts.parser()
                        .verifyWith(key) // Verify signature with secret key.
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();

                // Step 5: Extract email and authorities from claims.
                String email = String.valueOf(claims.get("email"));
                String authorities = String.valueOf(claims.get("authorities"));

                // Step 6: Convert authorities string → GrantedAuthority list.
                List<GrantedAuthority> auth =
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                // Step 7: Create Authentication object with email + authorities.
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(email, null, auth);

                // Step 8: Store authentication in SecurityContext.
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // If token is invalid → throw BadCredentialsException.
                throw new BadCredentialsException("Invalid token provided ...", e);
            }
        }

        // Step 9: Continue filter chain (pass request to next filter/controller).
        filterChain.doFilter(request, response);
    }
}