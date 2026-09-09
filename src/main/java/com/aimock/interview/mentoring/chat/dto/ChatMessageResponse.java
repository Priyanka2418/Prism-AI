package com.aimock.interview.mentoring.chat.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatMessageResponse(
        Long id,
        UUID senderId,
        String content,
        LocalDateTime createdAt
) {
}