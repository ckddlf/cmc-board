package com.example.board.service;

import com.example.board.domain.category.Category;
import com.example.board.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Long create(String name) {

        if (categoryRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 카테고리");
        }

        Category category = new Category(name);
        return categoryRepository.save(category).getId();
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}