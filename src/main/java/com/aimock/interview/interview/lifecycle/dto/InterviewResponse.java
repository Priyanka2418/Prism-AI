package com.aimock.interview.interview.lifecycle.dto;

import com.aimock.interview.common.enums.ExperienceLevel;
import com.aimock.interview.interview.commons.enums.Difficulty;
import com.aimock.interview.interview.commons.enums.InterviewStatus;
import com.aimock.interview.interview.commons.enums.InterviewType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InterviewResponse(
        UUID id,
        InterviewType interviewType,
        Difficulty interviewDifficulty,
        String targetRole,
        ExperienceLevel experienceLevel,
        List<String> topics,
        Integer durationMinutes,
        InterviewStatus status,
        LocalDateTime startedAt,
        LocalDateTime expiresAt,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}