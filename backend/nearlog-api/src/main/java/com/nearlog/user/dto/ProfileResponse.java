package com.nearlog.user.dto;

public record ProfileResponse(

        Long id,

        String username,

        String nickname,

        String bio,

        String profileImageUrl

) {
}