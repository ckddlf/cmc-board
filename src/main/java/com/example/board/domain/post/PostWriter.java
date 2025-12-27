package com.example.board.domain.post;

public interface PostWriter {
    Post save(Post post);
    void delete(Post post);
}