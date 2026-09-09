package com.aimock.interview.mentoring.request.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateMentorRequest(

        UUID interviewId,

        @NotNull(message = "Requested start time is required")
        @Future(message = "Requested start time must be in the future")
        LocalDateTime requestedStartTime,

        @NotNull(message = "Requested duration is required")
        @Min(value = 30, message = "Requested duration must be at least 30 minutes")
        @Max(value = 120, message = "Requested duration must not exceed 120 minutes")
        Integer requestedDurationMinutes,

        @Size(max = 2000, message = "Request message must not exceed 2000 characters")
        String requestMessage) {

}
