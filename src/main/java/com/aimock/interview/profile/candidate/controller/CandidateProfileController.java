package com.aimock.interview.profile.candidate.controller;

import com.aimock.interview.profile.candidate.dto.CandidateProfileRequest;
import com.aimock.interview.profile.candidate.dto.CandidateProfileResponse;
import com.aimock.interview.profile.candidate.service.CandidateProfileService;
import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<CandidateProfileResponse> createProfile(
            @Valid @RequestBody CandidateProfileRequest request) {

        CandidateProfileResponse response =
                studentProfileService.createProfile(request);

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

    @PutMapping("/me")
    public ResponseEntity<CandidateProfileResponse> updateMyProfile(
            @Valid @RequestBody CandidateProfileRequest request) {

        return ResponseEntity.ok(
                studentProfileService.updateMyProfile(request)
        );
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile() {

        studentProfileService.deleteMyProfile();

        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<CandidateProfileResponse> getProfileById(
//            @PathVariable UUID id) {
//
//        return ResponseEntity.ok(
//                studentProfileService.getProfileById(id)
//        );
//    }

//    @GetMapping
//    public ResponseEntity<List<CandidateProfileResponse>> getAllProfiles() {
//
//        return ResponseEntity.ok(
//                studentProfileService.getAllProfiles()
//        );
//    }
}