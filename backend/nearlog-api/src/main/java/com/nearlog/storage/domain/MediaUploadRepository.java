package com.nearlog.storage.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MediaUploadRepository
        extends JpaRepository<MediaUpload, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT m
        FROM MediaUpload m
        JOIN FETCH m.user
        WHERE m.id = :id
          AND m.user.id = :userId
        """)
    Optional<MediaUpload> findOwnedForUpdate(
            @Param("id")
            UUID id,

            @Param("userId")
            Long userId
    );
}