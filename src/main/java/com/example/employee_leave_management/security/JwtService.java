package com.example.employee_leave_management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "EmployeeLeaveManagementSystemSecretKey2026Secure";

    private SecretKey getKey() {

        return Keys.hmacShaKeyFor(
                SECRET.getBytes(
                        StandardCharsets.UTF_8
                )
        );
    }

    public String generateToken(
            String email,
            String role,
            Long employeeId) {

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("employeeId", employeeId)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 24 * 60 * 60 * 1000
                        )
                )
                .signWith(getKey())
                .compact();
    }

    public Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {

        return getClaims(token)
                .getSubject();
    }

    public String extractRole(String token) {

        return getClaims(token)
                .get("role", String.class);
    }

    public Long extractEmployeeId(String token) {

        Number id =
                getClaims(token)
                        .get("employeeId", Number.class);

        return id == null
                ? null
                : id.longValue();
    }
}
