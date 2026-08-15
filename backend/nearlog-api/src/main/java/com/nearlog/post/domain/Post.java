package com.nearlog.post.domain;

import com.nearlog.user.domain.User;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor(
        access = AccessLevel.PROTECTED
)
public class Post {

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
            name = "author_id",
            nullable = false
    )
    private User author;

    @Column(
            nullable = false,
            length = 2200
    )
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostVisibility visibility;

    @Column(name = "like_count")
    private long likeCount;

    @Column(name = "comment_count")
    private long commentCount;

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("sortOrder ASC")
    private List<PostMedia> media =
            new ArrayList<>();

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public static Post create(
            User author,
            String content,
            PostVisibility visibility
    ) {

        Post post =
                new Post();

        post.author =
                author;

        post.content =
                content == null
                        ? ""
                        : content.trim();

        post.visibility =
                visibility;

        post.createdAt =
                Instant.now();

        post.updatedAt =
                post.createdAt;

        return post;
    }

    public void addMedia(
            String objectKey,
            String contentType,
            int sortOrder
    ) {

        this.media.add(
                PostMedia.create(
                        this,
                        objectKey,
                        contentType,
                        sortOrder
                )
        );
    }
}