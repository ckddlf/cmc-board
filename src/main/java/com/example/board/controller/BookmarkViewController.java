package com.example.board.controller;

import com.example.board.domain.bookmark.Bookmark;
import com.example.board.domain.bookmark.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkViewController {

    private final BookmarkRepository bookmarkRepository;

    /**
     * 북마크 목록 페이지
     */
    @GetMapping
    public String bookmarkList(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        List<Bookmark> bookmarks = bookmarkRepository.findByUserUsername(userDetails.getUsername());
        
        // Lazy 로딩 초기화
        bookmarks.forEach(bookmark -> {
            bookmark.getPost().getTitle();
            bookmark.getPost().getUser().getUsername();
            bookmark.getPost().getCategories().size();
            bookmark.getPost().getComments().size();
        });
        
        model.addAttribute("bookmarks", bookmarks);
        
        return "bookmarks/list";
    }
}