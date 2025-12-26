package com.example.board.service;

import com.example.board.domain.comment.Comment;
import com.example.board.domain.comment.CommentRepository;
import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostRepository;
import com.example.board.domain.user.User;
import com.example.board.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Long create(String username, Long postId, Long parentId, String content) {

        User user = userRepository.findByUsername(username).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Comment parent = parentId == null ? null :
                commentRepository.findById(parentId).orElseThrow();

        Comment comment = new Comment(post, user, parent, content);
        return commentRepository.save(comment).getId();
    }

    public void delete(Long commentId, String username) {

        Comment comment = commentRepository.findById(commentId).orElseThrow();

        if (!comment.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("본인 댓글만 삭제 가능");
        }

        commentRepository.delete(comment);
    }
}