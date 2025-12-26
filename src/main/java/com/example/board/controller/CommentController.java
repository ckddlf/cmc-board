package com.example.board.controller;

import com.example.board.domain.comment.Comment;
import com.example.board.domain.comment.CommentRepository;
import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostRepository;
import com.example.board.domain.user.User;
import com.example.board.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @PostMapping
    public Long create(@AuthenticationPrincipal UserDetails userDetails,
                       @RequestParam Long postId,
                       @RequestParam(required = false) Long parentId,
                       @RequestParam String content) {

        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();
        Comment parent = parentId == null ? null :
                commentRepository.findById(parentId).orElseThrow();

        Comment comment = new Comment(post, user, parent, content);
        return commentRepository.save(comment).getId();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetails userDetails) {

        Comment comment = commentRepository.findById(id).orElseThrow();

        if (!comment.getUser().getUsername().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("본인 댓글만 삭제 가능");
        }
        commentRepository.delete(comment);
    }
}