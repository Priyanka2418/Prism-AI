package com.aimock.interview.profile.mentor.controller;

import com.aimock.interview.profile.mentor.dto.MentorProfileRequest;
import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;
import com.aimock.interview.profile.mentor.dto.MentorPublicProfileResponse;
import com.aimock.interview.profile.mentor.dto.MentorPublicProfileUpdateRequest;
import com.aimock.interview.profile.mentor.service.MentorProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mentor-profiles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MENTOR')")
public class MentorProfileController {

    private final MentorProfileService mentorProfileService;

    // Mentor creates profile
    @PostMapping
    public ResponseEntity<MentorProfileResponse> createProfile(
            @Valid @RequestBody MentorProfileRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mentorProfileService.createProfile(request));
    }

    // Mentor's own profile
    @GetMapping("/me")
    public ResponseEntity<MentorProfileResponse> getMyProfile() {

        return ResponseEntity.ok(
                mentorProfileService.getMyProfile()
        );
    }


    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile() {

        mentorProfileService.deleteMyProfile();

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/public-profile")
    public ResponseEntity<MentorPublicProfileResponse> updatePublicProfile(
            @Valid @RequestBody MentorPublicProfileUpdateRequest request) {

        return ResponseEntity.ok(
                mentorProfileService.updatePublicProfile(request));
    }

    @GetMapping("/me/public-profile")
    public ResponseEntity<MentorPublicProfileResponse> getMyPublicProfile() {

        return ResponseEntity.ok(
                mentorProfileService.getMyPublicProfile());
    }
}