package com.nearlog.auth.application;

import com.nearlog.auth.domain.RefreshToken;
import com.nearlog.auth.domain.RefreshTokenRepository;
import com.nearlog.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository
            refreshTokenRepository;

    @Transactional
    public void save(
            User user,
            String rawToken,
            Instant expiresAt
    ) {

        String tokenHash =
                hash(rawToken);

        RefreshToken refreshToken =
                RefreshToken.create(
                        user,
                        tokenHash,
                        expiresAt
                );

        refreshTokenRepository.save(
                refreshToken
        );
    }

    @Transactional
    public Long consume(
            String rawToken
    ) {

        String tokenHash =
                hash(rawToken);

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "유효하지 않은 Refresh Token입니다."
                                )
                        );

        if (
                refreshToken.isExpired()
                        || refreshToken.isRevoked()
        ) {

            throw new IllegalArgumentException(
                    "만료되었거나 폐기된 Refresh Token입니다."
            );
        }

        refreshToken.revoke();

        return refreshToken
                .getUser()
                .getId();
    }

    @Transactional
    public void revoke(
            String rawToken
    ) {

        refreshTokenRepository
                .findByTokenHash(
                        hash(rawToken)
                )
                .ifPresent(
                        RefreshToken::revoke
                );
    }

    private String hash(
            String rawToken
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hashed =
                    digest.digest(
                            rawToken.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat
                    .of()
                    .formatHex(hashed);

        } catch (
                NoSuchAlgorithmException e
        ) {

            throw new IllegalStateException(
                    e
            );
        }
    }
}