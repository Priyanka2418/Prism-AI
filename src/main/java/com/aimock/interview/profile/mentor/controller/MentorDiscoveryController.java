package com.aimock.interview.profile.mentor.controller;

import com.aimock.interview.profile.mentor.dto.MentorPublicProfileResponse;
import com.aimock.interview.profile.mentor.service.MentorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mentors")
@RequiredArgsConstructor
public class MentorDiscoveryController {

    private final MentorProfileService mentorProfileService;

    @GetMapping
    public ResponseEntity<List<MentorPublicProfileResponse>> getMentors() {
        return ResponseEntity.ok(
                mentorProfileService.getPublicMentors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MentorPublicProfileResponse> getMentorProfile(
            @PathVariable UUID id) {

        return ResponseEntity.ok(
                mentorProfileService.getPublicMentorProfile(id)
        );
    }
}