package com.nearlog.post.presentation;

import com.nearlog.common.security.UserPrincipal;
import com.nearlog.post.application.PostService;
import com.nearlog.post.dto.CreatePostRequest;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreatePostResponse create(

            @AuthenticationPrincipal
            UserPrincipal principal,

            @Valid
            @RequestBody
            CreatePostRequest request
    ) {

        Long postId =
                postService.createPost(
                        principal.getId(),
                        request
                );

        return new CreatePostResponse(
                postId
        );
    }

    public record CreatePostResponse(
            Long id
    ) {
    }
}