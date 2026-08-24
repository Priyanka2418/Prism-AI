package com.aimock.interview.profile.mentor.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MentorProfileRequest {

    @Size(max = 150)
    private String displayName;

    @Size(max = 150)
    private String company;

    @Size(max = 150)
    private String jobTitle;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "999.9")
    private BigDecimal yearsOfExperience;

    @Size(max = 500)
    private String linkedinUrl;
}