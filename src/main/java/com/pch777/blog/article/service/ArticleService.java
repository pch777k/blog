package com.pch777.blog.article.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.repository.ArticleRepository;
import com.pch777.blog.article.dto.ArticleDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    @Transactional(readOnly = true)
    public Article getArticleById(UUID id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Article> getArticles() {
        return articleRepository.findAll();
    }

    @Transactional
    public Article createArticle(ArticleDto articleDto) {
        Article article = articleMapper.map(articleDto);
        return articleRepository.save(article);
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
        articleRepository.deleteById(id);
    }

}
