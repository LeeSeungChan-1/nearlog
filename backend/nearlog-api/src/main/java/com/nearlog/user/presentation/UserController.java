package com.nearlog.user.presentation;

import com.nearlog.common.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

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
}