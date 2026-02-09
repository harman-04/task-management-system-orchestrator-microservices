// Package declaration → groups related classes together.
package com.example.task_user_service.taskSecurityConfig;

// JwtConstant → Utility class that stores constant values used in JWT authentication.
// These constants are referenced in JwtTokenValidator and other security components.
public class JwtConstant {

    // ================================
    // Secret Key
    // ================================
    // SECRET_KEY → Used to sign and validate JWT tokens.
    // This must be kept secure and private.
    // In production, store it in environment variables or a secure vault (not hardcoded).
    public static final String SECRET_KEY =
            "wpembytrwcvnryxksdbqwjebruyGHyudqgwveytrtrCSnwifoesarjbwF";

    // ================================
    // JWT Header
    // ================================
    // JWT_HEADER → Defines the HTTP header where JWT token will be passed.
    // Commonly "Authorization".
    // Example request:
    //   Authorization: Bearer <jwt_token>
    public static final String JWT_HEADER = "Authorization";
}