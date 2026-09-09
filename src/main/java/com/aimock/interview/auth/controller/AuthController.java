package com.aimock.interview.auth.controller;

import com.aimock.interview.auth.dto.AuthResponse;
import com.aimock.interview.auth.dto.LoginRequest;
import com.aimock.interview.auth.dto.RegisterRequest;
import com.aimock.interview.auth.security.AuthCookieService;
import com.aimock.interview.auth.service.AuthService;
import com.aimock.interview.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthCookieService authCookieService;

    @PostMapping("/register/candidate")
    public ResponseEntity<AuthResponse> registerCandidate(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse =
                authService.registerCandidate(request);

        authCookieService.addAccessTokenCookie(
                response,
                authResponse.getAccessToken()
        );

        authCookieService.addRefreshTokenCookie(
                response,
                authResponse.getRefreshToken()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authResponse);
    }

    @PostMapping("/register/mentor")
    public ResponseEntity<AuthResponse> registerMentor(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse =
                authService.registerMentor(request);

        authCookieService.addAccessTokenCookie(
                response,
                authResponse.getAccessToken()
        );

        authCookieService.addRefreshTokenCookie(
                response,
                authResponse.getRefreshToken()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse = authService.login(request);

        authCookieService.addAccessTokenCookie(
                response, authResponse.getAccessToken());

        authCookieService.addRefreshTokenCookie(
                response, authResponse.getRefreshToken());

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {

        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        AuthResponse authResponse =
                authService.refreshToken(refreshToken);

        authCookieService.addAccessTokenCookie(
                response,
                authResponse.getAccessToken()
        );

        authCookieService.addRefreshTokenCookie(
                response,
                authResponse.getRefreshToken()
        );

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            Authentication authentication,
            HttpServletResponse response) {

        User user =
                (User) authentication.getPrincipal();

        authService.logout(user);

        authCookieService.clearAuthCookies(response);

        return ResponseEntity.noContent().build();
    }
}