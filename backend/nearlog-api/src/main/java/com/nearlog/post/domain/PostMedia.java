package com.nearlog.post.domain;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(name = "post_media")
@NoArgsConstructor(
        access = AccessLevel.PROTECTED
)
public class PostMedia {

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
            name = "post_id",
            nullable = false
    )
    private Post post;

    @Column(
            name = "object_key",
            nullable = false
    )
    private String objectKey;

    @Column(
            name = "content_type",
            nullable = false
    )
    private String contentType;

    @Column(
            name = "sort_order",
            nullable = false
    )
    private int sortOrder;

    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    static PostMedia create(
            Post post,
            String objectKey,
            String contentType,
            int sortOrder
    ) {

        PostMedia media =
                new PostMedia();

        media.post =
                post;

        media.objectKey =
                objectKey;

        media.contentType =
                contentType;

        media.sortOrder =
                sortOrder;

        media.createdAt =
                Instant.now();

        return media;
    }
}