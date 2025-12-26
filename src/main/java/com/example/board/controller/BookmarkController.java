package com.example.board.controller;

import com.example.board.domain.bookmark.Bookmark;
import com.example.board.domain.bookmark.BookmarkRepository;
import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostRepository;
import com.example.board.domain.user.User;
import com.example.board.domain.user.UserRepository;
import com.example.board.dto.post.PostResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @PostMapping("/{postId}")
    public Long bookmark(@PathVariable Long postId,
                         @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Bookmark bookmark = new Bookmark(user, post);
        return bookmarkRepository.save(bookmark).getId();
    }

   @GetMapping
    public List<PostResponse> myBookmarks( @AuthenticationPrincipal UserDetails userDetails) {
        return bookmarkRepository.findByUserUsername(userDetails.getUsername())
            .stream()
            .map(Bookmark::getPost)
            .map(PostResponse::from)
            .toList();
    }

    
}