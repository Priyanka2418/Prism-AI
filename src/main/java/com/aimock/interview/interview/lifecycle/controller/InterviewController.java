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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PreAuthorize("hasAnyRole('CANDIDATE', 'MENTOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<InterviewResponse> createInterview(
            @Valid @RequestBody CreateInterviewRequest request) {

        InterviewResponse interview =
                interviewService.createInterview(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(interview);
    }

    @PreAuthorize("hasAnyRole('CANDIDATE', 'MENTOR', 'ADMIN')")
    @PostMapping("/{interviewId}/start")
    public ResponseEntity<InterviewResponse> startInterview(
            @PathVariable UUID interviewId) {

        InterviewResponse response =
                interviewService.startInterview(interviewId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('CANDIDATE', 'MENTOR', 'ADMIN')")
    @PostMapping("/{interviewId}/cancel")
    public ResponseEntity<InterviewResponse> cancelInterview(
            @PathVariable UUID interviewId) {

        InterviewResponse response =
                interviewService.cancelInterview(interviewId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('CANDIDATE', 'MENTOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<InterviewResponse>> getMyInterviews() {
        return ResponseEntity.ok(
                interviewService.getMyInterviews());
    }

    @PreAuthorize("hasAnyRole('CANDIDATE', 'MENTOR', 'ADMIN')")
    @PostMapping("/{interviewId}/complete")
    public ResponseEntity<InterviewResponse> completeInterview(
            @PathVariable UUID interviewId) {

        return ResponseEntity.ok(
                interviewService.completeInterview(interviewId));
    }

    @PreAuthorize("hasAnyRole('CANDIDATE', 'MENTOR', 'ADMIN')")
    @GetMapping("/{interviewId}")
    public ResponseEntity<InterviewResponse> getInterview(
            @PathVariable UUID interviewId) {
        return ResponseEntity.ok(
                interviewService.getInterview(interviewId));
    }

    @PreAuthorize("hasAnyRole('CANDIDATE', 'MENTOR', 'ADMIN')")
    @DeleteMapping("/{interviewId}")
    public ResponseEntity<Void> deleteInterview(
            @PathVariable UUID interviewId) {
        interviewService.deleteInterview(interviewId);
        return ResponseEntity.noContent().build();
    }
}