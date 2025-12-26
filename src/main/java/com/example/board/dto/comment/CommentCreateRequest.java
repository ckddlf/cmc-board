package com.example.board.dto.comment;

import lombok.Getter;

@Getter
public class CommentCreateRequest {
    private Long postId;
    private Long parentId;
    private String content;
}