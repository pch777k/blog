package com.pch777.blog.article.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.domain.repository.ArticleRepository;
import com.pch777.blog.article.domain.repository.ArticleStatsRepository;
import com.pch777.blog.article.dto.*;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.exception.resource.ArticleNotFoundException;
import com.pch777.blog.exception.authentication.ForbiddenException;
import com.pch777.blog.exception.authentication.UnauthorizedException;
import com.pch777.blog.notification.domain.model.NotificationType;
import com.pch777.blog.notification.service.NotificationService;
import com.pch777.blog.identity.author.domain.model.Author;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.pch777.blog.identity.user.domain.model.Role.ADMIN;

@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleStatsRepository articleStatsRepository;
    private final ArticleMapper articleMapper;
    private final BlogConfiguration blogConfiguration;
    private final ArticleStatsService articleStatsService;
    private final ArticleUtilsService articleUtilsService;
    private final UserService userService;
    private final NotificationService notificationService;


    @Transactional(readOnly = true)
    public Article getArticleById(UUID id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException(id));
    }

    @Transactional
    public Article getArticleByTitleUrl(String titleUrl) {
        Article article = articleRepository.findByTitleUrl(titleUrl)
                .orElseThrow(() -> new ArticleNotFoundException(titleUrl));
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

    public Page<ArticleAuthorPanelDto> getArticlesSummary(UUID authorId, String title, Pageable pageable) {
        return articleRepository.findSummaryByAuthorIdAndTitle(authorId, title, pageable);
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
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN') and hasAuthority('ARTICLE_CREATE')")
    public Article createArticleByAuthor(ArticleDto articleDto, String username) {
        User user = userService.getUserByUsername(username);
        if (!(user instanceof Author author)) {
            throw new IllegalArgumentException("User is not an author");
        }
        Article article = articleRepository.save(articleMapper.mapToArticle(new Article(), articleDto));
        article.setTitleUrl(articleUtilsService.generateUrlFromTitleAndId(article.getTitle(), article.getId()));
        article.setAuthor(author);
        author.getArticles().add(article);
        ArticleStats articleStats = new ArticleStats();
        articleStats.setTimeToRead(articleStatsService.calculateTimeToRead(article.getTitle(), article.getContent()));
        articleStats.setArticle(article);
        articleStatsRepository.save(articleStats);
        notificationService.createNotification(author, "New article created: " + article.getTitle(), NotificationType.ARTICLE);
        notificationService.createNotificationForSubscribers(article);
        return article;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN') and hasAuthority('ARTICLE_UPDATE')")
    public Article updateArticle(UUID id, ArticleDto articleDto, String username) {
        Article article = validateAndProcessArticle(id, username);
        ArticleStats articleStats = articleStatsService.getArticleStatsByArticleId(id);
        articleStats.setTimeToRead(articleStatsService.calculateTimeToRead(articleDto.getTitle(), articleDto.getContent()));
        article.setTitleUrl(articleUtilsService.generateUrlFromTitleAndId(articleDto.getTitle(), id));
        notificationService.createNotification(article.getAuthor(), "Article updated: " + article.getTitle(), NotificationType.ARTICLE);
        return articleMapper.mapToArticle(article, articleDto);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN') and hasAuthority('ARTICLE_DELETE')")
    public void deleteArticle(UUID id, String username) {
        Article article = validateAndProcessArticle(id, username);
        ArticleStats articleStats = articleStatsService.getArticleStatsByArticleId(id);
        articleStatsRepository.delete(articleStats);
        article.removeTags();
        articleRepository.delete(article);
        notificationService.createNotification(article.getAuthor(), "Article deleted: " + article.getTitle(), NotificationType.ARTICLE);
    }

    private Article validateAndProcessArticle(UUID articleId, String username) {
        if(username == null) {
            throw new UnauthorizedException("Unauthorized access");
        }
        User user = userService.getUserByUsername(username);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
        if (!user.getId().equals(article.getAuthor().getId()) && !user.getRole().equals(ADMIN)) {
            throw new ForbiddenException("Access denied");
        }
        return article;
    }

    @Transactional(readOnly = true)
    public ArticleNavigationDto getArticleNavigationDto(String titleUrl) {
        ArticleNavigationDto articleNavigationDto = new ArticleNavigationDto();
        Article article = articleRepository.findByTitleUrl(titleUrl)
                .orElseThrow(() -> new ArticleNotFoundException(titleUrl));
        List<Article> articles = articleRepository.findAll(Sort.by(blogConfiguration.getArticleSortField()));
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<ArticleShortDto> getTop3PopularArticles() {
        List<Article> articles = articleRepository.findTop3ArticlesOrderByViewsDesc();
        return articles.stream().map(articleMapper::mapToArticleShortDto).toList();
    }


    public Page<ArticleSummaryDto> getSummaryArticlesByAuthorId(UUID authorId, Pageable pageable) {
        Page<Article> articlesPage = articleRepository.findByAuthorId(authorId, pageable);
        return articlesPage.map(articleMapper::mapToArticleSummaryDto);
    }
}

