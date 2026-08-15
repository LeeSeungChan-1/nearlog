package com.nearlog.storage.presentation;

import com.nearlog.common.security.UserPrincipal;
import com.nearlog.storage.application.MediaUploadService;
import com.nearlog.storage.dto.PresignUploadRequest;
import com.nearlog.storage.dto.PresignUploadResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private final MediaUploadService uploadService;

    @PostMapping("/presign")
    public PresignUploadResponse presign(

            @AuthenticationPrincipal
            UserPrincipal principal,

            @Valid
            @RequestBody
            PresignUploadRequest request
    ) {

        return uploadService.presign(
                principal.getId(),
                request
        );
    }

    @PostMapping("/{uploadId}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(

            @AuthenticationPrincipal
            UserPrincipal principal,

            @PathVariable
            UUID uploadId
    ) {

        uploadService.complete(
                principal.getId(),
                uploadId
        );
    }
}