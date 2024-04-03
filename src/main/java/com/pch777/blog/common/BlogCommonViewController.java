package com.pch777.blog.common;

import com.pch777.blog.category.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;

@AllArgsConstructor
public abstract class BlogCommonViewController {

    protected CategoryService categoryService;

    protected void addGlobalAttributes(Model model) {
        model.addAttribute("categories", categoryService.getCategories());
    }
}