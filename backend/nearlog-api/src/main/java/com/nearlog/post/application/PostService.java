package com.nearlog.post.application;

import com.nearlog.common.exception.BusinessException;
import com.nearlog.common.exception.ErrorCode;
import com.nearlog.post.domain.Post;
import com.nearlog.post.domain.PostRepository;
import com.nearlog.post.dto.CreatePostRequest;
import com.nearlog.storage.application.MediaUploadService;
import com.nearlog.user.domain.User;
import com.nearlog.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final MediaUploadService mediaUploadService;

    @Transactional
    public Long createPost(
            Long userId,
            CreatePostRequest request
    ) {

        User author =
                userRepository
                        .findById(userId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.USER_NOT_FOUND
                                )
                        );

        if (
                request.mediaUploadIds()
                        .isEmpty()
                        ||
                        request.mediaUploadIds()
                                .size() > 10
        ) {

            throw new BusinessException(
                    ErrorCode.INVALID_POST_MEDIA_COUNT
            );
        }

        Post post =
                Post.create(
                        author,
                        request.content(),
                        request.visibility()
                );

        int sortOrder = 0;

        for (
                UUID uploadId
                : request.mediaUploadIds()
        ) {

            MediaUploadService.ConsumedUpload upload =
                    mediaUploadService
                            .consumePostUpload(
                                    userId,
                                    uploadId
                            );

            post.addMedia(
                    upload.objectKey(),
                    upload.contentType(),
                    sortOrder++
            );
        }

        Post saved =
                postRepository.save(post);

        return saved.getId();
    }
}