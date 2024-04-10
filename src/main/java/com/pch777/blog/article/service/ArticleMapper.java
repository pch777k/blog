package com.pch777.blog.article.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.dto.SummaryArticleDto;
import com.pch777.blog.category.domain.model.Category;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.tag.domain.model.Tag;
import com.pch777.blog.tag.dto.TagDto;
import com.pch777.blog.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class ArticleMapper {

    @Value("${article.shortContent.maxlength}")
    private int maxShortContentLength;
    private final ArticleStatsService articleStatsService;
    private final CategoryService categoryService;
    private final TagService tagService;

    public Article mapToArticle(Article article, ArticleDto articleDto) {
        Category category = categoryService.getCategoryById(articleDto.getCategoryId());
        article.setTitle(articleDto.getTitle());
        article.setContent(articleDto.getContent());
        article.setImageUrl(articleDto.getImageUrl());
        article.setCategory(category);

        List<TagDto> tagDtoList = articleDto.getTagDtoList();
        if (!tagDtoList.isEmpty()) {
            for (TagDto tagDto : tagDtoList) {
                if(!tagDto.getName().isBlank()) {
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
        ArticleDto articleDto = new ArticleDto();
        articleDto.setTitle(article.getTitle());
        articleDto.setContent(article.getContent());
        articleDto.setImageUrl(article.getImageUrl());
        if(!article.getTags().isEmpty()) {
            for (Tag tag : article.getTags()) {
                TagDto tagDto = new TagDto();
                tagDto.setName(tag.getName());
                articleDto.getTagDtoList().add(tagDto);
            }
        }
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

