package com.aimock.interview.mentoring.request.controller;

import com.aimock.interview.mentoring.request.dto.MentorRequestResponse;
import com.aimock.interview.mentoring.request.dto.RejectMentorRequest;
import com.aimock.interview.mentoring.request.service.MentorRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mentor/mentoring-requests")
@PreAuthorize("hasRole('MENTOR')")
@RequiredArgsConstructor
public class MentorRequestController {

    private final MentorRequestService mentorRequestService;

    @GetMapping
    public ResponseEntity<List<MentorRequestResponse>> getMentorPendingRequests() {

        return ResponseEntity.ok(
                mentorRequestService.getMentorPendingRequests()
        );
    }

    @PatchMapping("/{requestId}/accept")
    public ResponseEntity<MentorRequestResponse> acceptRequest(
            @PathVariable UUID requestId) {

        return ResponseEntity.ok(
                mentorRequestService.acceptRequest(requestId)
        );
    }

    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<MentorRequestResponse> rejectRequest(
            @PathVariable UUID requestId,
            @Valid @RequestBody RejectMentorRequest request) {

        return ResponseEntity.ok(
                mentorRequestService.rejectRequest(
                        requestId,
                        request
                )
        );
    }
}