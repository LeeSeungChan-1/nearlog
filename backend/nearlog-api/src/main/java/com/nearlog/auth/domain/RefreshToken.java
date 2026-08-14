package com.nearlog.auth.domain;

import com.nearlog.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            name = "token_hash",
            nullable = false,
            length = 64,
            unique = true
    )
    private String tokenHash;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    public static RefreshToken create(
            User user,
            String tokenHash,
            Instant expiresAt
    ) {

        RefreshToken token =
                new RefreshToken();

        token.user = user;
        token.tokenHash = tokenHash;
        token.expiresAt = expiresAt;
        token.createdAt = Instant.now();

        return token;
    }

    public void revoke() {

        if (revokedAt == null) {
            revokedAt = Instant.now();
        }
    }

    public boolean isExpired() {

        return expiresAt.isBefore(
                Instant.now()
        );
    }

    public boolean isRevoked() {

        return revokedAt != null;
    }
}