package com.example.board.controller;

import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostRepository;
import com.example.board.domain.user.User;
import com.example.board.domain.user.UserRepository;
import com.example.board.dto.post.PostResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @PostMapping
    public Long create(@AuthenticationPrincipal UserDetails userDetails,
                       @RequestBody Post request) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Post post = new Post(request.getTitle(), request.getContent(), user);
        return postRepository.save(post).getId();
    }

   @GetMapping
public List<PostResponse> list() {
    return postRepository.findAll()
        .stream()
        .map(PostResponse::from)
        .toList();
}

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetails userDetails,
                       @RequestBody Post request) {

        Post post = postRepository.findById(id).orElseThrow();

        if (!post.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("본인 글만 수정 가능");
        }
        post.update(request.getTitle(), request.getContent());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetails userDetails) {

        Post post = postRepository.findById(id).orElseThrow();

        if (!post.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("본인 글만 삭제 가능");
        }
        postRepository.delete(post);
    }
}