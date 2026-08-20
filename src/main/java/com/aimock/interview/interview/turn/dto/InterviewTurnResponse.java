package com.aimock.interview.interview.turn.dto;

import com.aimock.interview.interview.commons.enums.AiAction;
import com.aimock.interview.interview.commons.enums.Difficulty;
import com.aimock.interview.interview.commons.enums.Speaker;
import com.aimock.interview.interview.commons.enums.TurnType;

import java.time.LocalDateTime;
import java.util.UUID;

public record InterviewTurnResponse(
        Long id,
        UUID interviewId,
        Integer turnNumber,
        Speaker speaker,
        TurnType turnType,
        String content,
        Long parentTurnId,
        String topic,
        Difficulty difficulty,
        AiAction aiAction,
        Integer answerDurationSeconds,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        LocalDateTime createdAt
) {
}

