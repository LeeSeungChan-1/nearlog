package com.nearlog.user.presentation;

import com.nearlog.user.domain.User;

public record UserResponse(

        Long id,
        String email,
        String username,
        String nickname,
        String profileImageKey
) {

    public static UserResponse from(User user) {

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getNickname(),
                user.getProfileImageKey()
        );
    }
}