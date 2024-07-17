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
    Page<Article> findByAuthorIdAndTitleContainingIgnoreCase(UUID id, String title, Pageable pageable);

//    @Query("SELECT new com.pch777.blog.article.dto.ArticleSummaryDto(a.id, a.title, a.titleUrl, SUBSTRING(a.content, 1, 100), " +
//            "a.imageUrl, a.category.id, a.category.name, a.created, s.timeToRead, s.views, s.likes, (SELECT COUNT(c) FROM Comment c WHERE c.article.id = a.id)) " +
//            "FROM articles a " +
//            "LEFT JOIN article_stats s ON a.id = s.article.id " +
//            "WHERE a.author.id = :authorId AND a.title LIKE %:title%")
//    Page<ArticleSummaryDto> findSummaryByAuthorIdAndTitleContainingIgnoreCase(@Param("authorId") UUID authorId, @Param("title") String title, Pageable pageable);

//    @Query("SELECT new com.pch777.blog.article.dto.ArticleSummaryDto(a.id, a.title, a.titleUrl, SUBSTRING(a.content, 1, 100), " +
//            "a.imageUrl, a.category.id, a.category.name, a.created, s.timeToRead, s.views, s.likes, (SELECT COUNT(c) FROM Comment c WHERE c.article.id = a.id)) " +
//            "FROM articles a " +
//            "LEFT JOIN article_stats s ON a.id = s.article.id " +
//            "WHERE a.author.id = :authorId AND a.title LIKE %:title%")
//    Page<ArticleSummaryDto> findSummaryByAuthorIdAndTitleContainingIgnoreCase(
//            @Param("authorId") UUID authorId,
//            @Param("title") String title,
//            Pageable pageable);

//    @Query("SELECT new com.pch777.blog.article.dto.ArticleAuthorPanelDto" +
//            "(a.id, a.title, a.titleUrl, a.category.name, a.created, s.timeToRead, s.views, s.likes, " +
//            "(SELECT COUNT(c) FROM Comment c WHERE c.article.id = a.id)) " +
//            "FROM Article a " +
//            "LEFT JOIN a.stats s " +
//            "LEFT JOIN a.category cat " +
//            "WHERE a.author.id = :authorId AND a.title LIKE %:title%")
//    Page<ArticleAuthorPanelDto> findSummaryArticlesByAuthorIdAndTitle(@Param("authorId") UUID authorId, @Param("title") String title, Pageable pageable);




//    @Query(value = "SELECT new com.pch777.blog.article.dto.ArticleSummaryDto(a.id, a.title, a.titleUrl, " +
//            "SUBSTRING(a.content, 1, 100), a.imageUrl, a.category.id, a.category.name, a.created, " +
//            "s.timeToRead, s.views, s.likes, " +
//            "(SELECT COUNT(c) FROM Comment c WHERE c.article.id = a.id)) " +
//            "FROM articles a" +
//            "LEFT JOIN article_stats s " +
//            "WHERE a.author.id = :authorId AND LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))")
//    Page<ArticleSummaryDto> findSummaryByAuthorIdAndTitleContainingIgnoreCase(@Param("authorId") UUID authorId,
//                                                                              @Param("title") String title,
//                                                                              Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Article a WHERE a.id = :articleId AND a.author.id = :authorId")
    boolean existsByAuthorIdAndArticleId(@Param("authorId") UUID authorId, @Param("articleId") UUID articleId);
}
