package com.aimock.interview.profile.candidate.dto;

import com.aimock.interview.common.enums.ExperienceLevel;
import com.aimock.interview.common.enums.PreferredDomain;
import com.aimock.interview.common.enums.TargetRole;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class CandidateProfileRequest {
    @Size(max = 100)
    private String college;

    @Size(max = 100)
    private String degree;

    private ExperienceLevel experienceLevel;

    private PreferredDomain preferredDomain;

    private List<String> skills;

    @Size(max = 500)
    private String resumeUrl;

    private TargetRole targetRole;
}
