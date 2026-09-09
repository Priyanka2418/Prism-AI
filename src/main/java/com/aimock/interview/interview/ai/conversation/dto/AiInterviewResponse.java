package com.aimock.interview.interview.ai.conversation.dto;

import com.aimock.interview.interview.commons.enums.AiAction;
import com.aimock.interview.interview.commons.enums.AnswerPerformance;
import com.aimock.interview.interview.commons.enums.Difficulty;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiInterviewResponse(
        @JsonProperty("performance")
        AnswerPerformance performance,

        @JsonProperty("aiAction")
        @JsonAlias({"action", "actionType"})
        AiAction aiAction,

        @JsonProperty("difficulty")
        Difficulty difficulty,

        @JsonProperty("topic")
        String topic,

        @JsonProperty("content")
        @JsonAlias({"question", "questionText", "message", "response"})
        String content
) {
}