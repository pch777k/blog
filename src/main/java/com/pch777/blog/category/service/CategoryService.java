package com.pch777.blog.category.service;

import com.pch777.blog.category.domain.model.Category;
import com.pch777.blog.category.domain.repository.CategoryRepository;
import com.pch777.blog.category.dto.CategoryDto;
import com.pch777.blog.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Category getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new CategoryNotFoundException(name));
    }

    @Transactional
    public Category createCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(UUID id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        category.setName(categoryDto.getName());
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }

    public boolean isCategoryExists(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    public Page<Category> getCategories(String search, Pageable pageable) {
        return categoryRepository.findAllByNameContainingIgnoreCase(search, pageable);
    }
}
