package com.example.board.service;

import com.example.board.domain.bookmark.Bookmark;
import com.example.board.domain.bookmark.BookmarkRepository;
import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostRepository;
import com.example.board.domain.user.User;
import com.example.board.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public Long bookmark(String username, Long postId) {

        User user = userRepository.findByUsername(username).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Bookmark bookmark = new Bookmark(user, post);
        return bookmarkRepository.save(bookmark).getId();
    }

    
}