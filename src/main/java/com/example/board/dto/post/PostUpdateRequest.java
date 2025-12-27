package com.example.board.dto.post;

import lombok.Getter;

import java.util.List;

@Getter
public class PostUpdateRequest {
    private String title;
    private String content;
    private List<Long> categoryIds;

}