package com.nearlog.user.presentation;

import com.nearlog.common.security.UserPrincipal;
import com.nearlog.user.application.UserService;
import com.nearlog.user.dto.UpdateProfileImageRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public MeResponse me(

            @AuthenticationPrincipal
            UserPrincipal principal
    ) {

        return new MeResponse(
                principal.getId(),
                principal.getEmail(),
                principal.getProfileUsername(),
                principal.getNickname(),
                principal.getRole().name()
        );
    }

    public record MeResponse(

            Long id,
            String email,
            String username,
            String nickname,
            String role

    ) {
    }

    @PutMapping("/me/profile-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateProfileImage(

            @AuthenticationPrincipal
            UserPrincipal principal,

            @Valid
            @RequestBody
            UpdateProfileImageRequest request
    ) {

        userService.updateProfileImage(
                principal.getId(),
                request.uploadId()
        );
    }
}