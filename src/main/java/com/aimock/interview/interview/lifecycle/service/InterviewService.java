package com.aimock.interview.interview.service;

import com.aimock.interview.interview.dto.CreateInterviewRequest;
import com.aimock.interview.interview.dto.InterviewResponse;

import java.util.UUID;

public interface InterviewService {

    InterviewResponse createInterview(
            CreateInterviewRequest request);

    InterviewResponse startInterview(UUID interviewId);

    InterviewResponse cancelInterview(UUID interviewId);

    InterviewResponse getInterview(
            UUID interviewId);

    void completeExpiredInterviews();
}