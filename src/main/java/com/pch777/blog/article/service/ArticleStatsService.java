package com.pch777.blog.article.service;

import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.domain.repository.ArticleStatsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ArticleStatsService {

    private final ArticleStatsRepository articleStatsRepository;

    public ArticleStats getArticleStatsByArticleId(UUID id) {
        return articleStatsRepository.findByArticleId(id)
                .orElseThrow(() -> new EntityNotFoundException("ArticleStats not found with article id: " + id));
    }
}
