package com.example.board.dto.post;

import lombok.Getter;

@Getter
public class PostCreateRequest {
    private String title;
    private String content;
}