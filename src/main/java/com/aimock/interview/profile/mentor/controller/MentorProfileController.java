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
@RequestMapping("/api/v1/mentor-profiles")
@RequiredArgsConstructor
public class MentorProfileController {

    private final MentorProfileService mentorProfileService;

    @PostMapping
    public ResponseEntity<MentorProfileResponse> createProfile(
            @Valid @RequestBody MentorProfileRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mentorProfileService.createProfile(request));
    }

    @GetMapping("/me")
    public ResponseEntity<MentorProfileResponse> getMyProfile() {

        return ResponseEntity.ok(
                mentorProfileService.getMyProfile()
        );
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<MentorProfileResponse> getProfileById(
//            @PathVariable UUID id) {
//
//        MentorProfileResponse response =
//                mentorProfileService.getProfileById(id);
//
//        return ResponseEntity.ok(response);
//    }

//    @GetMapping
//    public ResponseEntity<List<MentorProfileResponse>> getAllProfiles() {
//        List<MentorProfileResponse> profiles =
//                mentorProfileService.getAllProfiles();
//
//        return ResponseEntity.ok(profiles);
//    }

    @PutMapping("/me")
    public ResponseEntity<MentorProfileResponse> updateMyProfile(
            @Valid @RequestBody MentorProfileRequest request) {

        return ResponseEntity.ok(
                mentorProfileService.updateMyProfile(request)
        );
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyProfile() {

        mentorProfileService.deleteMyProfile();

        return ResponseEntity.noContent().build();
    }
}