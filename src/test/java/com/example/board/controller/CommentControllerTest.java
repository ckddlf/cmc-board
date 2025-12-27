package com.example.board.controller;

import com.example.board.config.TestSecurityConfig;
import com.example.board.domain.comment.Comment;
import com.example.board.domain.comment.CommentRepository;
import com.example.board.domain.post.Post;
import com.example.board.domain.post.PostRepository;
import com.example.board.domain.user.User;
import com.example.board.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(CommentController.class)
@Import(TestSecurityConfig.class)

class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CommentRepository commentRepository;

    @MockBean
    PostRepository postRepository;

    @MockBean
    UserRepository userRepository;

    /* =========================
       댓글 생성 성공
       ========================= */
    @Test
    @WithMockUser(username = "user1")
    void 댓글_생성_성공() throws Exception {

        User user = new User("user1", "pw", null);
        Post post = new Post("제목", "내용", user);

        given(userRepository.findByUsername("user1"))
                .willReturn(Optional.of(user));
        given(postRepository.findById(1L))
                .willReturn(Optional.of(post));
        given(commentRepository.save(any(Comment.class)))
                .willAnswer(invocation -> {
                    Comment c = invocation.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        mockMvc.perform(post("/comments")
                .with(csrf())
                .param("postId", "1")
                .param("content", "댓글 내용"))
            .andExpect(status().isOk());
    }

    /* =========================
       댓글 삭제 성공 (본인)
       ========================= */
    @Test
    @WithMockUser(username = "user1")
    void 댓글_삭제_성공() throws Exception {

        User user = new User("user1", "pw", null);
        Comment comment = new Comment(null, user, null, "내용");

        given(commentRepository.findById(1L))
                .willReturn(Optional.of(comment));

        mockMvc.perform(delete("/comments/1"))
            .andExpect(status().isOk());

        verify(commentRepository).delete(comment);
    }

    /* =========================
       댓글 삭제 실패 (타인)
       ========================= */
    @Test
    @WithMockUser(username = "user2")
    void 댓글_삭제_실패_타인댓글() throws Exception {

        User owner = new User("user1", "pw", null);
        Comment comment = new Comment(null, owner, null, "내용");

        given(commentRepository.findById(1L))
                .willReturn(Optional.of(comment));

        mockMvc.perform(delete("/comments/1"))
            .andExpect(status().isForbidden());
    }
}