package com.example.board.service;

import com.example.board.domain.comment.Comment;
import com.example.board.domain.comment.CommentRepository;
import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostRepository;
import com.example.board.domain.user.User;
import com.example.board.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* =========================
       댓글 생성 성공
       ========================= */
    @Test
    void 댓글_생성_성공() {
        User user = new User("user1", "pw", null);
        Post post = new Post("제목", "내용", user);
        Comment comment = new Comment(post, user, null, "댓글 내용");
        comment.setId(1L);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Long id = commentService.create("user1", 1L, null, "댓글 내용");

        assertEquals(1L, id);
        verify(commentRepository).save(any(Comment.class));
    }

    /* =========================
       댓글 삭제 성공 (본인)
       ========================= */
    @Test
    void 댓글_삭제_성공() {
        User user = new User("user1", "pw", null);
        Comment comment = new Comment(null, user, null, "내용");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.delete(1L, "user1");

        verify(commentRepository).delete(comment);
    }

    /* =========================
       댓글 삭제 실패 (타인)
       ========================= */
    @Test
    void 댓글_삭제_실패_타인댓글() {
        User owner = new User("user1", "pw", null);
        Comment comment = new Comment(null, owner, null, "내용");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            commentService.delete(1L, "user2");
        });

        assertEquals("본인 댓글만 삭제 가능", exception.getMessage());
        verify(commentRepository, never()).delete(any());
    }
}