package com.nearlog.auth.application;

import com.nearlog.auth.dto.AuthResponse;
import com.nearlog.auth.dto.LoginRequest;
import com.nearlog.auth.dto.SignupRequest;
import com.nearlog.common.exception.BusinessException;
import com.nearlog.common.exception.ErrorCode;
import com.nearlog.common.security.JwtProperties;
import com.nearlog.common.security.JwtTokenProvider;
import com.nearlog.common.security.UserPrincipal;
import com.nearlog.user.domain.User;
import com.nearlog.user.domain.UserRepository;
import com.nearlog.user.presentation.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public UserResponse signup(SignupRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase(Locale.ROOT);

        String username = request.username()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_EMAIL
            );
        }

        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_USERNAME
            );
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

        User user = User.create(
                email,
                encodedPassword,
                username,
                request.nickname().trim()
        );

        User savedUser =
                userRepository.save(user);

        return UserResponse.from(savedUser);
    }
    public record LoginResult(
            AuthResponse response,
            String refreshToken
    ) {
    }

    @Transactional
    public LoginResult login(
            LoginRequest request
    ) {

        Authentication authentication =
                authenticationManager.authenticate(

                        new UsernamePasswordAuthenticationToken(
                                request.email()
                                        .trim()
                                        .toLowerCase(Locale.ROOT),

                                request.password()
                        )
                );

        UserPrincipal principal =
                (UserPrincipal)
                        authentication.getPrincipal();

        User user =
                userRepository.findById(
                        principal.getId()
                ).orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );

        String accessToken =
                jwtTokenProvider
                        .createAccessToken(principal);

        String refreshToken =
                jwtTokenProvider
                        .createRefreshToken(
                                user.getId()
                        );

        refreshTokenService.save(
                user,
                refreshToken,
                Instant.now().plus(
                        jwtProperties.refreshExpiration()
                )
        );

        AuthResponse response =
                AuthResponse.of(
                        accessToken,
                        jwtProperties
                                .accessExpiration()
                                .toSeconds(),
                        UserResponse.from(user)
                );

        return new LoginResult(
                response,
                refreshToken
        );
    }

    @Transactional
    public LoginResult refresh(
            String oldRefreshToken
    ) {

        Long tokenUserId =
                jwtTokenProvider
                        .getUserIdFromRefreshToken(
                                oldRefreshToken
                        );

        Long storedUserId =
                refreshTokenService.consume(
                        oldRefreshToken
                );

        if (!tokenUserId.equals(storedUserId)) {

            throw new BusinessException(
                    ErrorCode.INVALID_REFRESH_TOKEN
            );
        }

        User user =
                userRepository.findById(
                        tokenUserId
                ).orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.USER_NOT_FOUND
                        )
                );

        UserPrincipal principal =
                UserPrincipal.from(user);

        String newAccessToken =
                jwtTokenProvider
                        .createAccessToken(principal);

        String newRefreshToken =
                jwtTokenProvider
                        .createRefreshToken(
                                user.getId()
                        );

        refreshTokenService.save(
                user,
                newRefreshToken,
                Instant.now().plus(
                        jwtProperties.refreshExpiration()
                )
        );

        AuthResponse response =
                AuthResponse.of(
                        newAccessToken,
                        jwtProperties
                                .accessExpiration()
                                .toSeconds(),
                        UserResponse.from(user)
                );

        return new LoginResult(
                response,
                newRefreshToken
        );
    }
}