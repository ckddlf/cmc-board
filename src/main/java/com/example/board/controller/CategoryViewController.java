package com.example.board.controller;

import com.example.board.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryViewController {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 관리 페이지
     */
    @GetMapping
    public String managePage(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/categories";
    }
}