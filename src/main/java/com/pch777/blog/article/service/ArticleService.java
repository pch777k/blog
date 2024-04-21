package com.pch777.blog.article.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.domain.repository.ArticleRepository;
import com.pch777.blog.article.domain.repository.ArticleStatsRepository;
import com.pch777.blog.article.dto.*;
import com.pch777.blog.common.configuration.BlogConfiguration;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ArticleService {

    public static final String ARTICLE_NOT_FOUND_WITH_ID = "Article not found with id : ";
    private final ArticleRepository articleRepository;
    private final ArticleStatsRepository articleStatsRepository;
    private final ArticleMapper articleMapper;
    private final BlogConfiguration blogConfiguration;
    private final ArticleStatsService articleStatsService;
    private final ArticleUtilsService articleUtilsService;


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
    public Page<ArticleSummaryDto> getSummaryArticlesByTagId(UUID tagId, Pageable pageable) {
        Page<Article> articlesPage = articleRepository.findByTagsId(tagId, pageable);
        return articlesPage.map(articleMapper::mapToArticleSummaryDto);
    }

    @Transactional(readOnly = true)
    public Page<ArticleSummaryDto> getSummaryArticlesByTagName(String tagName, Pageable pageable) {
        Page<Article> articlesPage = articleRepository.findByTagsName(tagName, pageable);
        return articlesPage.map(articleMapper::mapToArticleSummaryDto);
    }

    public Page<ArticleSummaryDto> getSummaryArticlesByCategoryName(String categoryName, Pageable pageable) {
        Page<Article> articlesPage = articleRepository.findByCategoryNameIgnoreCase(categoryName, pageable);
        return articlesPage.map(articleMapper::mapToArticleSummaryDto);
    }

    @Transactional(readOnly = true)
    public Page<ArticleSummaryDto> getSummaryArticles(Pageable pageable) {
        Page<Article> articlesPage = articleRepository.findAll(pageable);
        return articlesPage.map(articleMapper::mapToArticleSummaryDto);
    }

    @Transactional(readOnly = true)
    public Page<ArticleSummaryDto> getSummaryArticles(String search, Pageable pageable) {
        Page<Article> articlesPage;
        if (search == null || search.isBlank()) {
            articlesPage = articleRepository.findAll(pageable);
        } else {
            articlesPage = articleRepository.findByTitleContainingIgnoreCase(search.trim(), pageable);
        }
        return articlesPage.map(articleMapper::mapToArticleSummaryDto);
    }

    @Transactional(readOnly = true)
    public Page<ArticleSummaryDto> getSummaryArticlesByMonth(LocalDate monthDate, Pageable pageable) {
        LocalDate startDate = monthDate.withDayOfMonth(1);
        LocalDate endDate = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
        Page<Article> articlesPage = articleRepository.findByCreatedBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59), pageable);
        return articlesPage.map(articleMapper::mapToArticleSummaryDto);
    }

    @Transactional
    public Article createArticle(ArticleDto articleDto) {
        Article article = articleRepository.save(articleMapper.mapToArticle(new Article(), articleDto));
        article.setTitleUrl(articleUtilsService.generateUrlFromTitleAndId(article.getTitle(), article.getId()));
        ArticleStats articleStats = new ArticleStats();
        articleStats.setTimeToRead(articleStatsService.calculateTimeToRead(article.getTitle(), article.getContent()));
        articleStats.setArticle(article);
        articleStatsRepository.save(articleStats);
        return article;
    }

    @Transactional
    public Article updateArticle(UUID id, ArticleDto articleDto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ARTICLE_NOT_FOUND_WITH_ID + id));
        ArticleStats articleStats = articleStatsRepository.findByArticleId(id)
                .orElseThrow(() -> new EntityNotFoundException("ArticleStats not found for article with id : " + id));
        articleStats.setTimeToRead(articleStatsService.calculateTimeToRead(articleDto.getTitle(), articleDto.getContent()));
        article.setTitleUrl(articleUtilsService.generateUrlFromTitleAndId(articleDto.getTitle(), id));
        return articleMapper.mapToArticle(article, articleDto);
    }

    @Transactional
    public void deleteArticle(UUID id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ARTICLE_NOT_FOUND_WITH_ID + id));
        ArticleStats articleStats = articleStatsRepository.findByArticleId(id)
                .orElseThrow(() -> new EntityNotFoundException("ArticleStats not found for article with id : " + id));
        articleStatsRepository.delete(articleStats);
        article.removeTags();
        articleRepository.delete(article);
    }

    @Transactional
    public void increaseLikes(UUID id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ARTICLE_NOT_FOUND_WITH_ID + id));
        articleStatsRepository.incrementLikesByArticleId(article.getId());
    }

    @Transactional(readOnly = true)
    public ArticleNavigationDto getArticleNavigationDto(String titleUrl) {
        ArticleNavigationDto articleNavigationDto = new ArticleNavigationDto();
        Article article = articleRepository.findByTitleUrl(titleUrl)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with title : " + titleUrl));
        List<Article> articles = articleRepository.findAll(Sort.by("created"));
        int currentIndex = articles.indexOf(article);

        int articlesSize = articles.size();
        if (currentIndex >= 0 && articlesSize > 1) {
            if (currentIndex > 0) {
                articleNavigationDto.setPrev(articles.get(currentIndex - 1).getTitleUrl());
            }
            if (currentIndex < articlesSize - 1) {
                articleNavigationDto.setNext(articles.get(currentIndex + 1).getTitleUrl());
            }
        }
        return articleNavigationDto;
    }

    public List<ArticleArchiveDateDto> getNumberOfLastMonths() {
        List<ArticleArchiveDateDto> monthYearList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (int i = 0; i < blogConfiguration.getNumberOfLastMonths(); i++) {
            ArticleArchiveDateDto articleArchiveDateDto = new ArticleArchiveDateDto();
            articleArchiveDateDto.setMonth(currentDate.getMonth().toString().toLowerCase());
            articleArchiveDateDto.setYear(currentDate.getYear());
            monthYearList.add(articleArchiveDateDto);
            currentDate = currentDate.minusMonths(1);
        }

        return monthYearList;
    }

    public List<ArticleShortDto> getTop3PopularArticles() {
        List<Article> articles = articleRepository.findTop3ArticlesOrderByViewsDesc();
        return articles.stream().map(articleMapper::mapToArticleShortDto).toList();
    }




}

