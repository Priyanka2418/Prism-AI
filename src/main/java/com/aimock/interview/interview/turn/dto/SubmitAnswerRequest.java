package com.aimock.interview.interview.turn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record SubmitAnswerRequest(

        @NotBlank(message = "Answer content must not be blank")
        String content,

        @PositiveOrZero(message = "Answer duration must be zero or greater")
        Integer answerDurationSeconds

) {
}
