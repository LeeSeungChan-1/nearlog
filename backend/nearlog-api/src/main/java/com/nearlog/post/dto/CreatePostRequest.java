package com.nearlog.post.dto;

import com.nearlog.post.domain.PostVisibility;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreatePostRequest(

        @Size(max = 2200)
        String content,

        @NotNull
        PostVisibility visibility,

        @NotNull
        @Size(
                min = 1,
                max = 10
        )
        List<UUID> mediaUploadIds

) {
}