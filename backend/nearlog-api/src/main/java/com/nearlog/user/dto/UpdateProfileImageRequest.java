package com.nearlog.user.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateProfileImageRequest(

        @NotNull
        UUID uploadId

) {
}