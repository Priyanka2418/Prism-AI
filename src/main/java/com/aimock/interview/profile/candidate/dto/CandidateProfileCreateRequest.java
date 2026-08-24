package com.aimock.interview.profile.candidate.dto;

import com.aimock.interview.common.enums.ExperienceLevel;
import com.aimock.interview.common.enums.PreferredDomain;
import com.aimock.interview.common.enums.TargetRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class CandidateProfileCreateRequest {

    @NotBlank
    @Size(max = 100)
    private String displayName;

    @NotBlank
    @Size(max = 100)
    private String college;

    @NotBlank
    @Size(max = 100)
    private String degree;

    @NotNull
    private ExperienceLevel experienceLevel;

    @NotNull
    private PreferredDomain preferredDomain;

    @NotEmpty
    private List<@NotBlank String> skills;


    @Size(max = 500)
    private String resumeUrl;

    @NotNull
    private TargetRole targetRole;
}
