package com.aimock.interview.user.service;

import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.user.dto.UserResponse;
import com.aimock.interview.user.entity.User;
import com.aimock.interview.auth.security.SecurityUtils;
import com.aimock.interview.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final SecurityUtils securityUtils;

    @Override
    public UserResponse getCurrentUser() {
        User user = securityUtils.getCurrentUser();
        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteUser(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                                  "User not found with id: " + id));
        userRepository.delete(user);
    }

    private UserResponse mapToResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}