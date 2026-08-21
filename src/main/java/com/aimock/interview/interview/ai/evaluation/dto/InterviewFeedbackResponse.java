package com.aimock.interview.interview.ai.evaluation.dto;

import java.util.List;
import java.util.UUID;

public record InterviewFeedbackResponse(

        UUID interviewId,

        String targetRole,

        String interviewType,

        Integer overallScore,

        String overallReason,

        Integer answerQualityRating,

        String answerQualityReason,

        List<String> keyStrengths,

        List<String> developmentAreas,

        List<String> recommendedPractice

//        List<FeedbackDeepDive> deepDive
) {
}