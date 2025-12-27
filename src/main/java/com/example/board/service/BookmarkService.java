package com.example.board.service;

import com.example.board.domain.bookmark.Bookmark;
import com.example.board.domain.bookmark.BookmarkRepository;
import com.example.board.dto.post.PostResponse;
import com.example.board.factory.EntityFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final EntityFactory entityFactory; // Factory 사용

    public Long bookmark(String username, Long postId) {
        var user = entityFactory.getUser(username);
        var post = entityFactory.getPost(postId);

        Bookmark bookmark = new Bookmark(user, post);
        return bookmarkRepository.save(bookmark).getId();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getMyBookmarks(String username) {
        var user = entityFactory.getUser(username);
        return bookmarkRepository.findByUserUsername(user.getUsername())
                .stream()
                .map(Bookmark::getPost)
                .map(PostResponse::from)
                .toList();
    }
}