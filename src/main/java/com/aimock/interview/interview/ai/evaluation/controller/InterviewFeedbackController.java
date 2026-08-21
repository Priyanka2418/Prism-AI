package com.aimock.interview.interview.ai.evaluation.controller;


import com.aimock.interview.interview.ai.evaluation.dto.InterviewFeedbackResponse;
import com.aimock.interview.interview.ai.evaluation.service.InterviewFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/interviews/{interviewId}/feedback")
@RequiredArgsConstructor
public class InterviewFeedbackController {

    private final InterviewFeedbackService interviewFeedbackService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<InterviewFeedbackResponse> generateFeedback(
            @PathVariable UUID interviewId) {

        return ResponseEntity.ok(
                interviewFeedbackService.generateFeedback(interviewId));
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping
    public ResponseEntity<InterviewFeedbackResponse> getFeedback(
            @PathVariable UUID interviewId) {

        return ResponseEntity.ok(
                interviewFeedbackService.getFeedback(interviewId));
    }
}