package com.aimock.interview.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public record LoginRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password

) {
}