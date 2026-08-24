package com.aimock.interview.profile.candidate.controller;

import com.aimock.interview.profile.candidate.dto.CandidateProfileCreateRequest;
import com.aimock.interview.profile.candidate.dto.CandidateProfileResponse;
import com.aimock.interview.profile.candidate.dto.CandidateProfileUpdateRequest;
import com.aimock.interview.profile.candidate.service.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/student-profiles")
@RequiredArgsConstructor
public class CandidateProfileController {

    private final CandidateProfileService studentProfileService;

    @PostMapping
    public ResponseEntity<CandidateProfileResponse> createProfile(
            @Valid @RequestBody CandidateProfileCreateRequest request) {

        CandidateProfileResponse response = studentProfileService.createProfile(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<CandidateProfileResponse> getMyProfile() {

        return ResponseEntity.ok(
                studentProfileService.getMyProfile()
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<CandidateProfileResponse> updateMyProfile(
            @Valid @RequestBody CandidateProfileUpdateRequest request) {

        return ResponseEntity.ok(
                studentProfileService.updateMyProfile(request)
        );
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile() {

        studentProfileService.deleteMyProfile();

        return ResponseEntity.noContent().build();
    }
}