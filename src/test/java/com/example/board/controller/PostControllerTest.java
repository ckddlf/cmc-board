package com.example.board.controller;

import com.example.board.domain.post.PostRepository;
import com.example.board.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @MockBean
    PostRepository postRepository; 

    @Test
    @WithMockUser(username = "user1", roles = "USER")
    void 게시글_생성_성공() throws Exception {

        given(postService.create(any(), any(), any(), any()))
                .willReturn(1L);

        mockMvc.perform(post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "title": "제목",
                      "content": "내용",
                      "categoryIds": [1, 2]
                    }
                """))
            .andExpect(status().isOk());
    }
}