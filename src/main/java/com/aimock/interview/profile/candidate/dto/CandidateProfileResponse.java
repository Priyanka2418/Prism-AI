package com.aimock.interview.profile.candidate.dto;

import com.aimock.interview.common.enums.ExperienceLevel;
import com.aimock.interview.common.enums.PreferredDomain;
import com.aimock.interview.common.enums.TargetRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CandidateProfileResponse {

    private UUID id;
    private UUID userId;
    private String college;
    private String degree;
    private ExperienceLevel experienceLevel;
    private PreferredDomain preferredDomain;
    private List<String> skills;
    private String resumeUrl;
    private TargetRole targetRole;
}