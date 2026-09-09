package com.aimock.interview.user.service;

import com.aimock.interview.user.dto.UserResponse;


import java.util.List;
import java.util.UUID;

public interface UserService {

    UserResponse getCurrentUser();

    List<UserResponse> getAllUsers();

    void deleteUser(UUID id);
}