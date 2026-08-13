package com.aimock.interview.user.service;


import com.aimock.interview.user.dto.user_request.UserCreateRequest;
import com.aimock.interview.user.dto.user_response.UserResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse getUserById(UUID id);

    List<UserResponse> getAllUsers();


    void deleteUser(UUID id);

    UserResponse registerCandidate(@Valid UserCreateRequest request);

    UserResponse registerMentor(@Valid UserCreateRequest request);
}