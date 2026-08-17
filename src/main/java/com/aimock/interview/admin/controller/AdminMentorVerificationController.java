package com.aimock.interview.admin.controller;

import com.aimock.interview.admin.dto.MentorRejectionRequest;
import com.aimock.interview.admin.service.AdminMentorVerificationService;
import com.aimock.interview.profile.mentor.dto.MentorProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/mentors")
@RequiredArgsConstructor
public class AdminMentorVerificationController {

    private final AdminMentorVerificationService verificationService;

    @GetMapping
    public ResponseEntity<List<MentorProfileResponse>> getAllMentors() {
        return ResponseEntity.ok(
                verificationService.getAllMentors()
        );
    }

    @GetMapping("/pending")
    public ResponseEntity<List<MentorProfileResponse>> getPendingMentors() {
        return ResponseEntity.ok(
                verificationService.getPendingMentors()
        );
    }

    @GetMapping("/{mentorProfileId}")
    public ResponseEntity<MentorProfileResponse> getMentorForVerification(
            @PathVariable UUID mentorProfileId) {

        return ResponseEntity.ok(
                verificationService.getMentorForVerification(
                        mentorProfileId
                )
        );
    }

    @PatchMapping("/{mentorProfileId}/verify")
    public ResponseEntity<MentorProfileResponse> verifyMentor(
            @PathVariable UUID mentorProfileId) {

        return ResponseEntity.ok(
                verificationService.verifyMentor(mentorProfileId)
        );
    }

    @PatchMapping("/{mentorProfileId}/reject")
    public ResponseEntity<MentorProfileResponse> rejectMentor(
            @PathVariable UUID mentorProfileId,
            @Valid @RequestBody MentorRejectionRequest request) {

        return ResponseEntity.ok(
                verificationService.rejectMentor(
                        mentorProfileId,
                        request.rejectionReason()
                )
        );
    }
}
