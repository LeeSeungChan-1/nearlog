package com.nearlog.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8, max = 64)
        String password,

        @NotBlank
        @Size(min = 3, max = 20)
        @Pattern(
                regexp = "^[a-zA-Z0-9._]+$",
                message = "username은 영문, 숫자, '.', '_'만 사용할 수 있습니다."
        )
        String username,

        @NotBlank
        @Size(min = 2, max = 30)
        String nickname
) {
}