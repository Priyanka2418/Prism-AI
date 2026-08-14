package com.aimock.interview.profile.mentor.dto;

import com.aimock.interview.common.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MentorProfileResponse {

    private UUID id;

    private UUID userId;

    private String headline;

    private String company;

    private String jobTitle;

    private BigDecimal yearsOfExperience;

    private List<String> expertise;

    private String bio;

    private String linkedinUrl;

    private VerificationStatus verificationStatus;

    private UUID verifiedBy;

    private LocalDateTime verifiedAt;

    private String rejectionReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}