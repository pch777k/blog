package com.pch777.blog.article.domain.repository;

import com.pch777.blog.article.domain.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {
    Optional<Article> findByTitleUrl(String titleUrl);
    Page<Article> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Article> findByCategoryId(UUID id, Pageable pageable);
    Page<Article> findByCategoryNameIgnoreCase(String categoryName, Pageable pageable);
    Page<Article> findByTagsId(UUID tagId, Pageable pageable);

    Page<Article> findByTagsName(String tagName, Pageable pageable);
    Page<Article> findByCreatedBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    @Query(value = """
            SELECT a.id, a.title, a.title_url, a.content, a.image_url, a.category_id, a.created, a.modified, ast.views
            FROM articles a
            JOIN article_stats ast ON a.id = ast.article_id
            ORDER BY ast.views DESC
            LIMIT 3;
            """,
    nativeQuery = true)
    List<Article> findTop3ArticlesOrderByViewsDesc();



}
