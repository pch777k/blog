package com.pch777.blog.article.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.dto.ArticleShortDto;
import com.pch777.blog.article.dto.ArticleSummaryDto;
import com.pch777.blog.category.domain.model.Category;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.comment.service.CommentService;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.tag.domain.model.Tag;
import com.pch777.blog.tag.dto.TagDto;
import com.pch777.blog.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class ArticleMapper {

    private final ArticleStatsService articleStatsService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final CommentService commentService;
    private final BlogConfiguration blogConfiguration;

    public Article mapToArticle(Article article, ArticleDto articleDto) {
        Category category = categoryService.getCategoryById(articleDto.getCategoryId());
        article.setTitle(articleDto.getTitle());
        article.setContent(articleDto.getContent());
        article.setImageUrl(articleDto.getImageUrl());
        article.setCategory(category);
        article.removeTags();
        List<TagDto> tagDtoList = articleDto.getTagDtoList();
        if (!tagDtoList.isEmpty()) {
            for (TagDto tagDto : tagDtoList) {
                if (!tagDto.getName().isBlank()) {
                    Tag tag;
                    if (!tagService.isTagExists(tagDto.getName())) {
                        tag = tagService.createTag(tagDto);
                    } else {
                        tag = tagService.getTagByName(tagDto.getName());
                    }
                    article.addTag(tag);
                }
            }
        }
        return article;
    }

    public ArticleDto mapToArticleDto(Article article) {
        List<TagDto> tagDtoList = new ArrayList<>();
        if (!article.getTags().isEmpty()) {
            for (Tag tag : article.getTags()) {
                TagDto tagDto = new TagDto();
                tagDto.setName(tag.getName());
                tagDtoList.add(tagDto);
            }
        }
        return ArticleDto.builder()
                .title(article.getTitle())
                .content(article.getContent())
                .imageUrl(article.getImageUrl())
                .categoryId(article.getCategory().getId())
                .tagDtoList(tagDtoList)
                .build();
    }

    public ArticleSummaryDto mapToArticleSummaryDto(Article article) {
        ArticleStats articleStats = articleStatsService.getArticleStatsByArticleId(article.getId());
        String shortContent = shortenContent(article.getContent(), blogConfiguration.getArticleShortContentLength());
        int totalComments = commentService.getComments(article.getId()).size();

        return ArticleSummaryDto.builder()
                .id(article.getId())
                .authorName(article.getAuthor().getFullName())
                .title(article.getTitle())
                .titleUrl(article.getTitleUrl())
                .shortContent(shortContent)
                .imageUrl(article.getImageUrl())
                .categoryId(article.getCategory().getId())
                .categoryName(article.getCategory().getName())
                .created(article.getCreated())
                .timeToRead(articleStats.getTimeToRead())
                .likes(articleStats.getLikes())
                .views(articleStats.getViews())
                .totalComments(totalComments)
                .build();
    }

    public ArticleShortDto mapToArticleShortDto(Article article) {
        return ArticleShortDto.builder()
                .title(article.getTitle())
                .titleUrl(article.getTitleUrl())
                .imageUrl(article.getImageUrl())
                .created(article.getCreated()).build();
    }

    private String shortenContent(String content, int maxLength) {
        if (content.length() <= maxLength) {
            return content;
        } else {
            return content.substring(0, maxLength - 3) + "...";
        }
    }

}

