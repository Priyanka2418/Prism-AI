package com.aimock.interview.interview.ai.evaluation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InterviewFeedbackResponse(

        @JsonProperty("interviewId")
        UUID interviewId,

        @JsonProperty("targetRole")
        String targetRole,

        @JsonProperty("interviewType")
        String interviewType,

        @JsonProperty("overallScore")
        Integer overallScore,

        @JsonProperty("overallReason")
        String overallReason,

        @JsonProperty("answerQualityRating")
        Integer answerQualityRating,

        @JsonProperty("answerQualityReason")
        String answerQualityReason,

        @JsonProperty("keyStrengths")
        List<String> keyStrengths,

        @JsonProperty("developmentAreas")
        List<String> developmentAreas,

        @JsonProperty("recommendedPractice")
        List<String> recommendedPractice
) {
}