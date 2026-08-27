package com.aimock.interview.mentoring.session.dto;

import com.aimock.interview.mentoring.session.common.SessionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record MentorSessionResponse(

        UUID id,

        String otherParticipantName,

        UUID interviewId,

        LocalDateTime scheduledStartAt,

        LocalDateTime scheduledEndAt,

        SessionStatus status

) {
}