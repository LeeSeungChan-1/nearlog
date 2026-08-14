package com.nearlog.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties properties;

    public String createAccessToken(
            UserPrincipal principal
    ) {

        Instant now = Instant.now();

        Instant expiration =
                now.plus(properties.accessExpiration());

        return Jwts.builder()

                .issuer(properties.issuer())

                .subject(
                        String.valueOf(principal.getId())
                )

                .claim(
                        "type",
                        "access"
                )

                .claim(
                        "role",
                        principal.getRole().name()
                )

                .issuedAt(Date.from(now))

                .expiration(
                        Date.from(expiration)
                )

                .signWith(accessKey())

                .compact();
    }

    public String createRefreshToken(
            Long userId
    ) {

        Instant now = Instant.now();

        Instant expiration =
                now.plus(properties.refreshExpiration());

        return Jwts.builder()
                .issuer(properties.issuer())
                .subject(String.valueOf(userId))
                .id(UUID.randomUUID().toString())
                .claim(
                        "type",
                        "refresh"
                )
                .issuedAt(Date.from(now))
                .expiration(
                        Date.from(expiration)
                )
                .signWith(refreshKey())
                .compact();
    }

    public Long getUserIdFromAccessToken(
            String token
    ) {

        Claims claims =
                parseAccessToken(token);

        return Long.valueOf(
                claims.getSubject()
        );
    }

    public Long getUserIdFromRefreshToken(
            String token
    ) {

        Claims claims =
                parseRefreshToken(token);

        return Long.valueOf(
                claims.getSubject()
        );
    }

    public Claims parseAccessToken(
            String token
    ) {

        Claims claims =
                parse(token, accessKey());

        validateType(
                claims,
                "access"
        );

        return claims;
    }

    public Claims parseRefreshToken(
            String token
    ) {

        Claims claims =
                parse(token, refreshKey());

        validateType(
                claims,
                "refresh"
        );

        return claims;
    }

    private Claims parse(
            String token,
            SecretKey key
    ) {

        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(
                        properties.issuer()
                )
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private void validateType(
            Claims claims,
            String expectedType
    ) {

        String type =
                claims.get(
                        "type",
                        String.class
                );

        if (!expectedType.equals(type)) {

            throw new IllegalArgumentException(
                    "잘못된 JWT 타입입니다."
            );
        }
    }

    private SecretKey accessKey() {

        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(
                        properties.accessSecret()
                )
        );
    }

    private SecretKey refreshKey() {

        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(
                        properties.refreshSecret()
                )
        );
    }
}