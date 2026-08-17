package com.aimock.interview.auth.service;

import com.aimock.interview.auth.dto.AuthResponse;
import com.aimock.interview.auth.dto.LoginRequest;
import com.aimock.interview.auth.dto.RefreshTokenRequest;
import jakarta.validation.Valid;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(@Valid RefreshTokenRequest request);
}
