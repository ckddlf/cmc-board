package com.example.board.controller;

import com.example.board.dto.post.PostResponse;
import com.example.board.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{postId}")
    public Long bookmark(@PathVariable Long postId,
                         @AuthenticationPrincipal UserDetails userDetails) {
        // Service로 위임
        return bookmarkService.bookmark(userDetails.getUsername(), postId);
    }

    @GetMapping
    public List<PostResponse> myBookmarks(@AuthenticationPrincipal UserDetails userDetails) {
        // Service로 위임
        return bookmarkService.getMyBookmarks(userDetails.getUsername());
    }
}