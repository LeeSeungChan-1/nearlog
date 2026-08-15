package com.nearlog.storage.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record PresignUploadResponse(

        UUID uploadId,

        String uploadUrl,

        String method,

        Map<String, String> headers,

        Instant expiresAt

) {
}