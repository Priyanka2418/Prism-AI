package com.aimock.interview.interview.ai.conversation.service;

import com.aimock.interview.interview.ai.conversation.context.InterviewAiContext;
import com.aimock.interview.interview.ai.conversation.dto.AiInterviewResponse;
import com.aimock.interview.interview.commons.enums.InterviewType;

public interface InterviewAiService {

    InterviewType getInterviewType();

    AiInterviewResponse generateNextQuestion(
            InterviewAiContext context
    );
}