package com.example.board.controller;

import com.example.board.domain.category.Category;
import com.example.board.config.TestSecurityConfig;
import com.example.board.domain.category.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import(TestSecurityConfig.class) 
class CategoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CategoryRepository categoryRepository;

    /* =========================
       카테고리 생성 성공
       ========================= */
    @Test
    void 카테고리_생성_성공() throws Exception {

        Category category = new Category("Spring");
        category.setId(1L);

        given(categoryRepository.save(any(Category.class)))
                .willReturn(category);

        mockMvc.perform(post("/categories")
                .param("name", "Spring"))
            .andExpect(status().isOk())
            .andExpect(content().string("1"));
    }

    /* =========================
       카테고리 목록 조회 성공
       ========================= */
    @Test
    void 카테고리_목록_조회_성공() throws Exception {

        List<Category> categories = List.of(
                new Category("Spring"),
                new Category("JPA")
        );

        given(categoryRepository.findAll())
                .willReturn(categories);

        mockMvc.perform(get("/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("Spring"))
            .andExpect(jsonPath("$[1].name").value("JPA"));
    }
}