package com.nearlog.storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(
        prefix = "app.storage.s3"
)
public record S3Properties(

        String bucket,

        String region,

        Duration uploadExpiration,

        Duration downloadExpiration,

        long profileMaxSize,

        long postMaxSize

) {
}