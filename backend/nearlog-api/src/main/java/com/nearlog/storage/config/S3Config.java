package com.nearlog.storage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {

    @Bean(destroyMethod = "close")
    public DefaultCredentialsProvider
    awsCredentialsProvider() {

        return DefaultCredentialsProvider.create();
    }

    @Bean(destroyMethod = "close")
    public S3Client s3Client(
            S3Properties properties,
            DefaultCredentialsProvider credentialsProvider
    ) {

        return S3Client.builder()
                .region(
                        Region.of(
                                properties.region()
                        )
                )
                .credentialsProvider(
                        credentialsProvider
                )
                .build();
    }

    @Bean(destroyMethod = "close")
    public S3Presigner s3Presigner(
            S3Properties properties,
            DefaultCredentialsProvider credentialsProvider
    ) {

        return S3Presigner.builder()
                .region(
                        Region.of(
                                properties.region()
                        )
                )
                .credentialsProvider(
                        credentialsProvider
                )
                .build();
    }
}