package com.pch777.blog.article.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.dto.SummaryArticleDto;
import com.pch777.blog.category.domain.model.Category;
import com.pch777.blog.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ArticleMapper {

    @Value("${article.shortContent.maxlength}")
    private int maxShortContentLength;
    private final CategoryService categoryService;
    private final ArticleStatsService articleStatsService;

    public Article map(ArticleDto articleDto) {
        Article article = new Article();
        Category category = categoryService.getCategoryById(articleDto.getCategoryId());

        article.setTitle(articleDto.getTitle());
        article.setContent(articleDto.getContent());
        article.setImageUrl(articleDto.getImageUrl());
        article.setCategory(category);

        return article;
    }

    public ArticleDto map(Article article) {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setTitle(article.getTitle());
        articleDto.setContent(article.getContent());
        articleDto.setImageUrl(article.getImageUrl());
        articleDto.setCategoryId(article.getCategory().getId());

        return articleDto;
    }

    public SummaryArticleDto mapToSummaryArticleDto(Article article) {
        ArticleStats articleStats = articleStatsService.getArticleStatsByArticleId(article.getId());
        return SummaryArticleDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .titleUrl(article.getTitleUrl())
                .shortContent(shortenContent(article.getContent(), maxShortContentLength))
                .imageUrl(article.getImageUrl())
                .categoryId(article.getCategory().getId())
                .categoryName(article.getCategory().getName())
                .created(article.getCreated())
                .timeToRead(article.getTimeToRead())
                .likes(articleStats.getLikes())
                .views(articleStats.getViews())
                .build();
    }

    private String shortenContent(String content, int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        } else {
            return content.substring(0, maxLength - 3) + "...";
        }
    }

}

