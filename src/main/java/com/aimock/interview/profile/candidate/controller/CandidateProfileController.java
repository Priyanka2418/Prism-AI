package com.aimock.interview.profile.candidate.controller;

import com.aimock.interview.profile.candidate.dto.CandidateProfileRequest;
import com.aimock.interview.profile.candidate.dto.CandidateProfileResponse;
import com.aimock.interview.profile.candidate.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/student-profiles")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService studentProfileService;

    @PostMapping("/{userId}")
    public ResponseEntity<CandidateProfileResponse> createProfile(
            @PathVariable UUID userId,
            @RequestBody CandidateProfileRequest request) {

        CandidateProfileResponse response =
                studentProfileService.createProfile(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateProfileResponse> getProfileById(
            @PathVariable UUID id) {
        CandidateProfileResponse response =
                studentProfileService.getProfileById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CandidateProfileResponse>> getAllProfiles() {
        List<CandidateProfileResponse> profiles =
                studentProfileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CandidateProfileResponse> getProfileByUserId(
            @PathVariable UUID userId) {

        CandidateProfileResponse response =
                studentProfileService.getProfileByUserId(userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidateProfileResponse> updateProfile(
            @PathVariable UUID id,
            @RequestBody CandidateProfileRequest request) {

        CandidateProfileResponse response =
                studentProfileService.updateProfile(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(
            @PathVariable UUID id) {

        studentProfileService.deleteProfile(id);

        return ResponseEntity.noContent().build();
    }

}