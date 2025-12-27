package com.example.board.controller;

import com.example.board.domain.post.PostRepository;
import com.example.board.dto.post.PostCreateRequest;
import com.example.board.dto.post.PostResponse;
import com.example.board.dto.post.PostUpdateRequest;
import com.example.board.service.PostService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;
    private final PostService postService;

     @PostMapping
    public Long create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PostCreateRequest request
    ) {
        return postService.create(
                userDetails.getUsername(),
                request.getTitle(),
                request.getContent(),
                request.getCategoryIds()
        );
    }

   @GetMapping
    public List<PostResponse> list() {
    return postRepository.findAll()
        .stream()
        .map(PostResponse::from)
        .toList();
    }

     @PutMapping("/{id}")
    public void update(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PostUpdateRequest request
    ) {
        postService.update(
                id,
                userDetails.getUsername(),
                request.getTitle(),
                request.getContent(),
                request.getCategoryIds()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        postService.delete(id, userDetails.getUsername());
    }
}