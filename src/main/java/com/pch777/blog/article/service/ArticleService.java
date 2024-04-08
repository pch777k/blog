package com.pch777.blog.article.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.domain.repository.ArticleRepository;
import com.pch777.blog.article.domain.repository.ArticleStatsRepository;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.dto.SummaryArticleDto;
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
    public List<SummaryArticleDto> getSummaryArticles() {
        return articleRepository.findAll()
                .stream()
                .map(articleMapper::mapToSummaryArticleDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<SummaryArticleDto> getSummaryArticles(Pageable pageable) {
        return getSummaryArticles(null, pageable);
    }

    @Transactional(readOnly = true)
    public Page<SummaryArticleDto> getSummaryArticles(String search, Pageable pageable) {
        Page<Article> articlesPage;
        if (search == null) {
            articlesPage = articleRepository.findAll(pageable);
        } else {
            articlesPage = articleRepository.findByTitleContainingIgnoreCase(search, pageable);
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
        Article updatedArticle = articleMapper.map(articleDto);
        updatedArticle.setId(article.getId());
        updatedArticle.setCreated(article.getCreated());
        return articleRepository.save(updatedArticle);
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
