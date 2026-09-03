package com.aimock.interview.auth.service;

import com.aimock.interview.auth.dto.AuthResponse;
import com.aimock.interview.auth.dto.LoginRequest;
import com.aimock.interview.auth.dto.RefreshTokenRequest;
import com.aimock.interview.user.entity.User;
import jakarta.validation.Valid;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshTToken);

    void logout(User user);

    AuthResponse authenticate(User user);
}
