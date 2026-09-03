package com.aimock.interview.user.controller;

import com.aimock.interview.auth.dto.AuthResponse;
import com.aimock.interview.auth.security.AuthCookieService;
import com.aimock.interview.user.dto.user_request.UserCreateRequest;
import com.aimock.interview.user.dto.user_response.UserResponse;
import com.aimock.interview.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthCookieService authCookieService;

    @PostMapping("/candidate")
    public ResponseEntity<Void> createCandidate(
            @Valid @RequestBody UserCreateRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse =
                userService.registerCandidate(request);

        authCookieService.addAccessTokenCookie(
                response, authResponse.getAccessToken());

        authCookieService.addRefreshTokenCookie(
                response, authResponse.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/mentor")
    public ResponseEntity<Void> createMentor(
            @Valid @RequestBody UserCreateRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse =
                userService.registerMentor(request);

        authCookieService.addAccessTokenCookie(
                response, authResponse.getAccessToken());

        authCookieService.addRefreshTokenCookie(
                response, authResponse.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}