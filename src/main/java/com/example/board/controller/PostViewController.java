package com.example.board.controller;

import com.example.board.domain.category.CategoryRepository;
import com.example.board.domain.post.Post;
import com.example.board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class PostViewController {

    private final PostService postService;
    private final CategoryRepository categoryRepository;

    /**
     * 게시판 메인 페이지 (게시글 목록)
     */
    @GetMapping
    public String listPosts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 15, sort = "createdAt", direction = Sort.Direction.DESC) 
            Pageable pageable,
            Model model) {
        
        Page<Post> posts;
        
        if (categoryId != null) {
            posts = postService.findByCategoryId(categoryId, pageable);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            posts = postService.searchByKeyword(keyword, pageable);
        } else {
            posts = postService.findAll(pageable);
        }
        
        model.addAttribute("posts", posts);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("keyword", keyword);
        
        return "posts/list";
    }

    /**
     * 게시글 상세 페이지
     */
    @GetMapping("/{id}")
    public String viewPost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        Post post = postService.findById(id);
        
        boolean isOwner = userDetails != null && 
                         post.getUser().getUsername().equals(userDetails.getUsername());
        
        model.addAttribute("post", post);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isAuthenticated", userDetails != null);
        
        return "posts/detail";
    }

    /**
     * 게시글 작성 페이지
     */
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "posts/write";
    }

    /**
     * 게시글 수정 페이지
     */
    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        
        Post post = postService.findById(id);
        
        if (!post.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        
        model.addAttribute("post", post);
        model.addAttribute("categories", categoryRepository.findAll());
        
        return "posts/edit";
    }
}