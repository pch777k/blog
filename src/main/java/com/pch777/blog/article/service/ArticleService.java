package com.pch777.blog.article.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.repository.ArticleRepository;
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
    @Transactional(readOnly = true)
    public Article getArticle(UUID id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Article> getArticles() {
        return articleRepository.findAll();
    }

    @Transactional
    public Article createArticle(Article articleRequest) {
        Article article = new Article();
        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        return articleRepository.save(article);
    }

    @Transactional
    public Article updateArticle(UUID id, Article articleRequest) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + id));
        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        return articleRepository.save(article);
    }

    @Transactional
    public void deleteArticle(UUID id) {
        articleRepository.deleteById(id);
    }

}
