package com.aimock.interview.mentoring.session.controller;

import com.aimock.interview.mentoring.session.dto.MentorSessionResponse;
import com.aimock.interview.mentoring.session.service.MentorSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mentor-sessions")
@RequiredArgsConstructor
public class MentorSessionController {

    private final MentorSessionService mentorSessionService;

    @GetMapping("/candidate")
    @PreAuthorize("hasRole('CANDIDATE')")

    public ResponseEntity<List<MentorSessionResponse>> getCandidateSessions() {

        return ResponseEntity.ok(
                mentorSessionService.getCandidateSessions());
    }

    @GetMapping("/mentor")
    @PreAuthorize("hasRole('MENTOR')")
    public ResponseEntity<List<MentorSessionResponse>> getMentorSessions() {

        return ResponseEntity.ok(
                mentorSessionService.getMentorSessions());
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<MentorSessionResponse> getSession(
            @PathVariable UUID sessionId) {

        return ResponseEntity.ok(
                mentorSessionService.getSession(sessionId));
    }
}
