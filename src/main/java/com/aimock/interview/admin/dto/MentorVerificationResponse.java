package com.aimock.interview.admin.dto;

import com.aimock.interview.common.enums.VerificationStatus;

import java.util.UUID;

public record MentorVerificationResponse(
        UUID mentorProfileId,
        VerificationStatus verificationStatus
) {
}