package com.example.board.service;

import com.example.board.domain.category.Category;
import com.example.board.domain.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /* =========================
       카테고리 생성 성공
       ========================= */
    @Test
    void 카테고리_생성_성공() {
        String name = "카테고리1";
        Category category = new Category(name);
        category.setId(1L);

        when(categoryRepository.findByName(name)).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Long id = categoryService.create(name);

        assertEquals(1L, id);
        verify(categoryRepository).save(any(Category.class));
    }

    /* =========================
       카테고리 생성 실패 (중복)
       ========================= */
    @Test
    void 카테고리_생성_실패_중복() {
        String name = "카테고리1";
        when(categoryRepository.findByName(name)).thenReturn(Optional.of(new Category(name)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.create(name);
        });

        assertEquals("이미 존재하는 카테고리", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    /* =========================
       모든 카테고리 조회
       ========================= */
    @Test
    void 카테고리_조회() {
        List<Category> categories = List.of(new Category("A"), new Category("B"));
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.findAll();

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getName());
        assertEquals("B", result.get(1).getName());
    }
}