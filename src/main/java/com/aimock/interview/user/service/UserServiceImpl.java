package com.aimock.interview.user.service;

import com.aimock.interview.common.enums.Role;
import com.aimock.interview.common.exception.DuplicateResourceException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.user.dto.user_request.UserCreateRequest;
import com.aimock.interview.user.dto.user_response.UserResponse;
import com.aimock.interview.user.entity.User;

import com.aimock.interview.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerCandidate(UserCreateRequest request) {
        return registerUser(request, Role.CANDIDATE);
    }

    @Override
    public UserResponse registerMentor(UserCreateRequest request) {
        return registerUser(request, Role.MENTOR);
    }

    private UserResponse registerUser(
            UserCreateRequest request,
            Role role) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already registered");
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                                "User not found with id: " + id));

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