package com.pch777.blog.article.domain.repository;

import com.pch777.blog.article.domain.model.ArticleStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ArticleStatsRepository extends JpaRepository<ArticleStats, UUID> {
    Optional<ArticleStats> findByArticleId(UUID id);

    @Modifying
    @Query("UPDATE article_stats a SET a.views = a.views + 1 WHERE a.article.id = :articleId")
    void incrementViewsByArticleId(@Param("articleId") UUID articleId);

    @Modifying
    @Query("UPDATE article_stats a SET a.likes = a.likes + 1 WHERE a.article.id = :articleId")
    void incrementLikesByArticleId(@Param("articleId") UUID articleId);
}
