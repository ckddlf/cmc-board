package com.example.board.dto.post;

import com.example.board.domain.post.Post;

import java.util.List;

public record PostResponse(
    Long id,
    String title,
    String content,
    String username,
    List<String> categories
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getUser().getUsername(),
            post.getPostCategories().stream()
                .map(pc -> pc.getCategory().getName())
                .toList()
        );
    }
}