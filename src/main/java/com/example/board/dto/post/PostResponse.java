package com.example.board.dto.post;

import com.example.board.domain.post.Post;

public record PostResponse(
    Long id,
    String title,
    String content,
    String username
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getUser().getUsername()
        );
    }
}