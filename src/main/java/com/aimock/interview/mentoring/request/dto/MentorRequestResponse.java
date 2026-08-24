package com.aimock.interview.mentoring.request.dto;

import com.aimock.interview.mentoring.request.enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record MentorRequestResponse(

        UUID id,

        String mentorName,

        UUID interviewId,

        LocalDateTime requestedStartTime,
        Integer requestedDurationMinutes,

        String requestMessage,

        RequestStatus status,

        LocalDateTime mentorAcceptedAt,
        String mentorRejectionReason,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
