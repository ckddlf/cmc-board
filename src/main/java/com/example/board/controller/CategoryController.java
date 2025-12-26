package com.example.board.controller;

import com.example.board.domain.category.Category;
import com.example.board.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @PostMapping
    public Long create(@RequestParam String name) {
        Category category = new Category(name);
        return categoryRepository.save(category).getId();
    }

    @GetMapping
    public List<Category> list() {
        return categoryRepository.findAll();
    }
}