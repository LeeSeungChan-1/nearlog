package com.nearlog.storage.dto;

import com.nearlog.storage.domain.UploadPurpose;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PresignUploadRequest(

        @NotNull
        UploadPurpose purpose,

        @NotBlank
        String fileName,

        @NotBlank
        String contentType,

        @Positive
        long fileSize

) {
}