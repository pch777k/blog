package com.pch777.blog.article.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.category.domain.model.Category;
import com.pch777.blog.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ArticleMapper {

    private final CategoryService categoryService;

    public Article map(ArticleDto articleDto) {
        Article article = new Article();
        Category category = categoryService.getCategoryById(articleDto.getCategoryId());

        article.setTitle(articleDto.getTitle());
        article.setContent(articleDto.getContent());
        article.setCategory(category);

        return article;
    }

    public ArticleDto map(Article article) {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setTitle(article.getTitle());
        articleDto.setContent(article.getContent());
        articleDto.setCategoryId(article.getCategory().getId());

        return articleDto;
    }
}

