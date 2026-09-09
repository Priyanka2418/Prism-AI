package com.aimock.interview.profile.mentor.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class MentorPublicProfileResponse {

    private UUID id;

    private String displayName;

    private String company;

    private String jobTitle;

    private BigDecimal yearsOfExperience;

    private String profileImageUrl;

    private String headline;

    private String bio;

    private List<String> expertise;

    private String linkedinUrl;
}