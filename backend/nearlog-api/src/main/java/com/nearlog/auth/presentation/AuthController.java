package com.nearlog.auth.presentation;

import com.nearlog.auth.application.AuthService;
import com.nearlog.auth.dto.AuthResponse;
import com.nearlog.auth.dto.LoginRequest;
import com.nearlog.auth.dto.SignupRequest;
import com.nearlog.user.presentation.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(
            @Valid @RequestBody SignupRequest request
    ) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid
            @RequestBody
            LoginRequest request
    ) {

        AuthService.LoginResult result =
                authService.login(request);

        ResponseCookie refreshCookie =
                createRefreshCookie(
                        result.refreshToken()
                );

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.SET_COOKIE,
                        refreshCookie.toString()
                )

                .body(
                        result.response()
                );
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(

            @CookieValue(
                    name = "refresh_token",
                    required = false
            )
            String refreshToken
    ) {

        if (refreshToken == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        AuthService.LoginResult result =
                authService.refresh(
                        refreshToken
                );

        ResponseCookie cookie =
                createRefreshCookie(
                        result.refreshToken()
                );

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.SET_COOKIE,
                        cookie.toString()
                )

                .body(
                        result.response()
                );
    }

    private ResponseCookie createRefreshCookie(
            String refreshToken
    ) {

        return ResponseCookie
                .from(
                        "refresh_token",
                        refreshToken
                )
                .httpOnly(true)
                .secure(false) // 배포 시 true값으로 변경
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(
                        Duration.ofDays(14)
                )
                .build();
    }
}