package com.pch777.blog.article.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.domain.repository.ArticleRepository;
import com.pch777.blog.article.domain.repository.ArticleStatsRepository;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.dto.SummaryArticleDto;
import com.pch777.blog.category.domain.model.Category;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.tag.domain.model.Tag;
import com.pch777.blog.tag.dto.TagDto;
import com.pch777.blog.tag.service.TagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleStatsRepository articleStatsRepository;
    private final ArticleMapper articleMapper;
    private final CategoryService categoryService;
    private final TagService tagService;

    @Transactional(readOnly = true)
    public Article getArticleById(UUID id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));
    }

    @Transactional
    public Article getArticleByTitleUrl(String titleUrl) {
        Article article = articleRepository.findByTitleUrl(titleUrl)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with title : " + titleUrl));
        articleStatsRepository.incrementViewsByArticleId(article.getId());
        return article;
    }

    @Transactional(readOnly = true)
    public List<Article> getArticles() {
        return articleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<SummaryArticleDto> getSummaryArticlesByTagId(UUID tagId, String search, Pageable pageable) {
        Page<Article> articlesPage;
        if (search == null || search.isBlank()) {
            articlesPage = articleRepository.findByTagsId(tagId, pageable);
        } else {
            articlesPage = articleRepository.findByTagsIdAndTitleContainingIgnoreCase(tagId, search.trim(), pageable);
        }
        return articlesPage.map(articleMapper::mapToSummaryArticleDto);
    }

    @Transactional(readOnly = true)
    public Page<SummaryArticleDto> getSummaryArticles(UUID categoryId, String search, Pageable pageable) {
        Page<Article> articlesPage;
        if (search == null || search.isBlank()) {
            articlesPage = articleRepository.findByCategoryId(categoryId, pageable);
        } else {
            articlesPage = articleRepository.findByCategoryIdAndTitleContainingIgnoreCase(categoryId, search.trim(), pageable);
        }
        return articlesPage.map(articleMapper::mapToSummaryArticleDto);
    }

    @Transactional(readOnly = true)
    public Page<SummaryArticleDto> getSummaryArticles(String search, Pageable pageable) {
        Page<Article> articlesPage;
        if (search == null || search.isBlank()) {
            articlesPage = articleRepository.findAll(pageable);
        } else {
            articlesPage = articleRepository.findByTitleContainingIgnoreCase(search.trim(), pageable);
        }
        return articlesPage.map(articleMapper::mapToSummaryArticleDto);
    }

    @Transactional
    public Article createArticle(ArticleDto articleDto) {
        Article article = articleRepository.save(articleMapper.map(articleDto));
        ArticleStats articleStats = new ArticleStats();
        articleStats.setArticle(article);
        articleStatsRepository.save(articleStats);

        return article;
    }

    @Transactional
    public Article updateArticle(UUID id, ArticleDto articleDto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));
        Category category = categoryService.getCategoryById(articleDto.getCategoryId());

        article.setTitle(articleDto.getTitle());
        article.setContent(articleDto.getContent());
        article.setImageUrl(articleDto.getImageUrl());

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
        article.setCategory(category);
        return article;
    }

    @Transactional
    public void deleteArticle(UUID id) {
        ArticleStats articleStats = articleStatsRepository.findByArticleId(id)
                .orElseThrow(() -> new EntityNotFoundException("ArticleStats not found for article with id : " + id));
        articleStatsRepository.delete(articleStats);
        articleRepository.deleteById(id);
    }

    @Transactional
    public void increaseLikes(UUID id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id : " + id));
        articleStatsRepository.incrementLikesByArticleId(article.getId());
    }
}
