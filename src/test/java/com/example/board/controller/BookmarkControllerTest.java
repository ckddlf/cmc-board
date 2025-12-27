package com.example.board.controller;

import com.example.board.domain.bookmark.Bookmark;
import com.example.board.domain.bookmark.BookmarkRepository;
import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostRepository;
import com.example.board.domain.user.User;
import com.example.board.domain.user.UserRepository;
import com.example.board.dto.post.PostResponse;
import com.example.board.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookmarkController.class)
@Import(TestSecurityConfig.class)
class BookmarkControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookmarkRepository bookmarkRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    PostRepository postRepository;

    /* =========================
       북마크 등록 성공
       ========================= */
    @Test
    @WithMockUser(username = "user1")
    void 북마크_등록_성공() throws Exception {

        User user = new User("user1", "pw", null);
        Post post = new Post("제목", "내용", user);
        Bookmark bookmark = new Bookmark(user, post);

        given(userRepository.findByUsername("user1"))
                .willReturn(Optional.of(user));
        given(postRepository.findById(1L))
                .willReturn(Optional.of(post));
        given(bookmarkRepository.save(any(Bookmark.class)))
                .willReturn(bookmark);

        mockMvc.perform(post("/bookmarks/1"))
            .andExpect(status().isOk());
    }

    /* =========================
       내 북마크 조회 성공
       ========================= */
    @Test
    @WithMockUser(username = "user1")
    void 내_북마크_조회_성공() throws Exception {

        User user = new User("user1", "pw", null);
        Post post = new Post("제목", "내용", user);
        Bookmark bookmark = new Bookmark(user, post);

        given(bookmarkRepository.findByUserUsername("user1"))
                .willReturn(List.of(bookmark));

        mockMvc.perform(get("/bookmarks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("제목"));
    }
}