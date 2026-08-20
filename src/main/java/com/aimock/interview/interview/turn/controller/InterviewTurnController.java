package com.aimock.interview.interview.turn.controller;

import com.aimock.interview.interview.turn.dto.InterviewTurnResponse;
import com.aimock.interview.interview.turn.dto.SubmitAnswerRequest;
import com.aimock.interview.interview.turn.service.InterviewTurnService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/interviews/{interviewId}/turns")
@RequiredArgsConstructor
public class InterviewTurnController {

    private final InterviewTurnService interviewTurnService;

    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/start")
    public ResponseEntity<InterviewTurnResponse> startFirstTurn(
            @PathVariable UUID interviewId) {

        return ResponseEntity.ok(
                interviewTurnService.startFirstTurn(interviewId));
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @PostMapping("/{questionTurnId}/answer")
    public ResponseEntity<InterviewTurnResponse> submitAnswer(
            @PathVariable UUID interviewId,
            @PathVariable Long questionTurnId,
            @Valid @RequestBody SubmitAnswerRequest request) {

        return ResponseEntity.ok(
                interviewTurnService.submitAnswer(
                        interviewId, questionTurnId, request));
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping
    public ResponseEntity<List<InterviewTurnResponse>> getInterviewTurns(
            @PathVariable UUID interviewId) {

        return ResponseEntity.ok(
                interviewTurnService.getInterviewTurns(interviewId));
    }
}