package com.aimock.interview.auth.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);
}
