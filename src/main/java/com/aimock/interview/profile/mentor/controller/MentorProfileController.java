package com.aimock.interview.profile.mentor.controller;

import com.aimock.interview.profile.mentor.dto.MentorProfileRequest;
import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;
import com.aimock.interview.profile.mentor.service.MentorProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mentors")
@RequiredArgsConstructor
public class MentorProfileController {

    private final MentorProfileService mentorProfileService;

    @PostMapping("/profile")
    public ResponseEntity<MentorProfileResponse> createProfile(
            @RequestParam UUID userId,
            @Valid @RequestBody MentorProfileRequest request
    ) {

        MentorProfileResponse response =
                mentorProfileService.createProfile(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<MentorProfileResponse> getMyProfile(
            @RequestParam UUID userId
    ) {

        MentorProfileResponse response =
                mentorProfileService.getProfileByUserId(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MentorProfileResponse> getProfileById(
            @PathVariable UUID id
    ) {

        MentorProfileResponse response =
                mentorProfileService.getProfileById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MentorProfileResponse>> getAllProfiles() {

        List<MentorProfileResponse> profiles =
                mentorProfileService.getAllProfiles();

        return ResponseEntity.ok(profiles);
    }

    @PutMapping("/profile")
    public ResponseEntity<MentorProfileResponse> updateProfile(
            @RequestParam UUID userId,
            @Valid @RequestBody MentorProfileRequest request
    ) {

        MentorProfileResponse response =
                mentorProfileService.updateProfile(userId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfile(
            @RequestParam UUID userId
    ) {

        mentorProfileService.deleteProfile(userId);

        return ResponseEntity.noContent().build();
    }
}