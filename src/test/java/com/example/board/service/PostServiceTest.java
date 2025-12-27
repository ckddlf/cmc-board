package com.example.board.service;

import com.example.board.domain.category.Category;
import com.example.board.domain.category.CategoryRepository;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* =========================
       글 생성 성공
       ========================= */
    @Test
    void 글_생성_성공() {
        User user = new User("user1", "pw", null);
        Category category = new Category("카테고리");
        Post post = new Post("제목", "내용", user);
        post.setId(1L);

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Long id = postService.create("user1", "제목", "내용", List.of(1L));

        assertEquals(1L, id);
        verify(postRepository).save(any(Post.class));
    }

    /* =========================
       글 수정 성공 (본인)
       ========================= */
    @Test
    void 글_수정_성공() {
        User user = new User("user1", "pw", null);
        Post post = new Post("OldTitle", "OldContent", user);
        post.setId(1L);
        Category category = new Category("새카테고리");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        postService.update(1L, "user1", "NewTitle", "NewContent", List.of(2L));

        assertEquals("NewTitle", post.getTitle());
        assertEquals("NewContent", post.getContent());
        assertEquals(1, post.getPostCategories().size());
    }

    /* =========================
       글 수정 실패 (타인)
       ========================= */
    @Test
    void 글_수정_실패_타인() {
        User user = new User("user1", "pw", null);
        Post post = new Post("제목", "내용", user);
        post.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
            postService.update(1L, "user2", "NewTitle", "NewContent", List.of())
        );

        assertEquals("본인 글만 수정 가능", ex.getMessage());
    }

    /* =========================
       글 삭제 성공 (본인)
       ========================= */
    @Test
    void 글_삭제_성공() {
        User user = new User("user1", "pw", null);
        Post post = new Post("제목", "내용", user);
        post.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.delete(1L, "user1");

        verify(postRepository).delete(post);
    }

    /* =========================
       글 삭제 실패 (타인)
       ========================= */
    @Test
    void 글_삭제_실패_타인() {
        User user = new User("user1", "pw", null);
        Post post = new Post("제목", "내용", user);
        post.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
            postService.delete(1L, "user2")
        );

        assertEquals("본인 글만 삭제 가능", ex.getMessage());
        verify(postRepository, never()).delete(any());
    }
}