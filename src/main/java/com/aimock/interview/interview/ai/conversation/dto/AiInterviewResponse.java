package com.aimock.interview.interview.ai.conversation.dto;

import com.aimock.interview.interview.commons.enums.AiAction;
import com.aimock.interview.interview.commons.enums.AnswerPerformance;
import com.aimock.interview.interview.commons.enums.Difficulty;

public record AiInterviewResponse(
        AnswerPerformance performance,
        AiAction aiAction,
        Difficulty difficulty,
        String topic,
        String content
) {
}