package com.nearlog.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(

        String issuer,

        String accessSecret,
        String refreshSecret,

        Duration accessExpiration,
        Duration refreshExpiration
) {
}