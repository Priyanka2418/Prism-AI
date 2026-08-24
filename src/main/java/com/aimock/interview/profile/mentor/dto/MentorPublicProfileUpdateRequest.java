package com.aimock.interview.profile.mentor.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class MentorPublicProfileUpdateRequest {

    @NotBlank
    @Size(max = 150)
    private String displayName;

    @NotBlank
    @Size(max = 200)
    private String headline;

    @NotBlank
    @Size(max = 2000)
    private String bio;

    @NotBlank
    @Size(max = 150)
    private String company;

    @NotBlank
    @Size(max = 150)
    private String jobTitle;

    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "999.9")
    private BigDecimal yearsOfExperience;

    @NotEmpty
    private List<String> expertise;

    @Size(max = 500)
    private String profileImageUrl;

    @NotBlank
    @Size(max = 500)
    private String linkedinUrl;
}