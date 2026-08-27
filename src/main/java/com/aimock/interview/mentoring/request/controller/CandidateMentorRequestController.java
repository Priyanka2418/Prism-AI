package com.aimock.interview.mentoring.request.controller;

import com.aimock.interview.mentoring.request.dto.CreateMentorRequest;
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
@RequestMapping("/api/v1/mentors")
@PreAuthorize("hasRole('CANDIDATE')")
@RequiredArgsConstructor
public class CandidateMentorRequestController {

    private final MentorRequestService mentorRequestService;

    @PostMapping("/{mentorId}/mentoring-requests")
    public ResponseEntity<MentorRequestResponse> createRequest(
            @PathVariable UUID mentorId,
            @Valid @RequestBody CreateMentorRequest request) {

        MentorRequestResponse response =
                mentorRequestService.createRequest(mentorId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/candidate-mentoring-requests")
    public ResponseEntity<List<MentorRequestResponse>> getCandidateRequests() {

        return ResponseEntity.ok(
                mentorRequestService.getCandidateRequests());
    }
}
