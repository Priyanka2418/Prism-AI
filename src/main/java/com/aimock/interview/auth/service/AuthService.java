package com.aimock.interview.auth.service;

import com.aimock.interview.auth.dto.AuthResponse;
import com.aimock.interview.auth.dto.LoginRequest;
import com.aimock.interview.auth.dto.RegisterRequest;
import com.aimock.interview.user.entity.User;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshTToken);

    AuthResponse registerCandidate(RegisterRequest request);

    AuthResponse registerMentor(RegisterRequest request);

    void logout(User user);

    AuthResponse authenticate(User user);
}
