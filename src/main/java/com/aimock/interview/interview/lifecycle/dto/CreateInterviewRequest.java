package com.aimock.interview.interview.dto;

import com.aimock.interview.common.enums.ExperienceLevel;
import com.aimock.interview.interview.enums.Difficulty;
import com.aimock.interview.interview.enums.InterviewType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateInterviewRequest(

        @NotNull
        InterviewType interviewType,

        @NotNull
        Difficulty interviewDifficulty,

        @NotBlank
        String targetRole,

        @NotNull
        ExperienceLevel experienceLevel,

        @NotEmpty
        List<String> topics,

        @NotNull
        @Min(30)
        @Max(120)
        Integer durationMinutes
) {
}