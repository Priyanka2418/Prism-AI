package com.aimock.interview.interview.ai.evaluation.service;


import com.aimock.interview.interview.ai.evaluation.dto.InterviewFeedbackResponse;
import java.util.UUID;

public interface InterviewFeedbackService {

    InterviewFeedbackResponse generateFeedback(UUID interviewId);

    InterviewFeedbackResponse getFeedback(UUID interviewId);
}