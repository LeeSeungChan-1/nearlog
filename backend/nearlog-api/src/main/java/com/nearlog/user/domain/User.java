package com.nearlog.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 30, unique = true)
    private String username;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 150)
    private String bio;

    @Column(name = "profile_image_key")
    private String profileImageKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private User(
            String email,
            String password,
            String username,
            String nickname
    ) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;

        this.role = UserRole.USER;
        this.status = UserStatus.ACTIVE;
    }

    public static User create(
            String email,
            String encodedPassword,
            String username,
            String nickname
    ) {
        return new User(
                email,
                encodedPassword,
                username,
                nickname
        );
    }

    @PrePersist
    private void prePersist() {

        Instant now = Instant.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public void updateProfileImage(String profileImageKey) {
        this.profileImageKey = profileImageKey;
    }
}