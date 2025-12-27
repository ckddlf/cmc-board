package com.example.board.service;

import com.example.board.domain.bookmark.Bookmark;
import com.example.board.domain.bookmark.BookmarkRepository;
import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostRepository;
import com.example.board.domain.user.User;
import com.example.board.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private BookmarkService bookmarkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 북마크_성공() {
        String username = "user1";
        Long postId = 1L;

        User user = new User("user1", "pw", null);
        Post post = new Post("제목", "내용", user);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(bookmarkRepository.save(any(Bookmark.class))).thenAnswer(invocation -> {
            Bookmark b = invocation.getArgument(0);
            b.setId(1L);
            return b;
        });

        Long bookmarkId = bookmarkService.bookmark(username, postId);

        assertEquals(1L, bookmarkId);
        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }

    @Test
    public void 북마크_실패_사용자없음() {
    String username = "user1";
    Long postId = 1L;

    when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

    try {
        bookmarkService.bookmark(username, postId);
        fail("예외가 발생해야 합니다.");
    } catch (RuntimeException e) {
        // 예외 발생 확인
    }

    verify(bookmarkRepository, never()).save(any());
    }

    @Test
    void 북마크_실패_게시글없음() {
        String username = "user1";
        Long postId = 1L;

        User user = new User("user1", "pw", null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // 게시글 없으면 예외 발생
        assertThrows(RuntimeException.class, () -> bookmarkService.bookmark(username, postId));

        verify(bookmarkRepository, never()).save(any());
    }
}