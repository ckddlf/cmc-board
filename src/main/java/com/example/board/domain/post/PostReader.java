package com.example.board.domain.post;

import java.util.List;
import java.util.Optional;

public interface PostReader {
    Optional<Post> findById(Long id);
    List<Post> findAll();
    List<Post> findByUserUsername(String username);
}