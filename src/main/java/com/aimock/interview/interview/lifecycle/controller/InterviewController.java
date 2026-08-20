package com.aimock.interview.interview.lifecycle.controller;

import com.aimock.interview.interview.lifecycle.dto.CreateInterviewRequest;
import com.aimock.interview.interview.lifecycle.dto.InterviewResponse;
import com.aimock.interview.interview.lifecycle.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping
    public ResponseEntity<InterviewResponse> createInterview(
            @Valid @RequestBody CreateInterviewRequest request) {

        InterviewResponse interview =
                interviewService.createInterview(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(interview);
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/{interviewId}/start")
    public ResponseEntity<InterviewResponse> startInterview(
            @PathVariable UUID interviewId) {

        InterviewResponse response =
                interviewService.startInterview(interviewId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/{interviewId}/cancel")
    public ResponseEntity<InterviewResponse> cancelInterview(
            @PathVariable UUID interviewId) {

        InterviewResponse response =
                interviewService.cancelInterview(interviewId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/{interviewId}")
    public ResponseEntity<InterviewResponse> getInterview(
            @PathVariable UUID interviewId) {
        return ResponseEntity.ok(
                interviewService.getInterview(interviewId));
    }
}