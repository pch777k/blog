package com.pch777.blog.article.domain.repository;

import com.pch777.blog.article.domain.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID>, ArticleRepositoryCustom {
    Optional<Article> findByTitleUrl(String titleUrl);
    Page<Article> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Article> findByCategoryNameIgnoreCase(String categoryName, Pageable pageable);
    Page<Article> findByTagsId(UUID tagId, Pageable pageable);

    Page<Article> findByTagsName(String tagName, Pageable pageable);
    Page<Article> findByCreatedBetween(LocalDateTime startDateTime, LocalDateTime endDateTime, Pageable pageable);

    @Query(value = """
            SELECT a.id, a.title, a.title_url, a.content, a.image_url, a.category_id, a.author_id, a.created, a.modified, ast.views
            FROM articles a
            JOIN article_stats ast ON a.id = ast.article_id
            ORDER BY ast.views DESC
            LIMIT 3;
            """,
    nativeQuery = true)
    List<Article> findTop3ArticlesOrderByViewsDesc();

    Page<Article> findByAuthorId(UUID id, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Article a WHERE a.id = :articleId AND a.author.id = :authorId")
    boolean existsByAuthorIdAndArticleId(@Param("authorId") UUID authorId, @Param("articleId") UUID articleId);
}
