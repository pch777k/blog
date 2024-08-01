package com.pch777.blog.article.domain.repository;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.UserArticleLike;
import com.pch777.blog.identity.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserArticleLikeRepository extends JpaRepository<UserArticleLike, UUID> {
    boolean existsByUserAndArticle(User user, Article article);

    boolean existsByUserIdAndArticleId(UUID userId, UUID articleId);

    @Query("SELECT al FROM user_article_likes al WHERE al.user.id = :id AND (" +
            "LOWER(al.article.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(al.article.author.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(al.article.author.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<UserArticleLike> findAllLikesByUserId(@Param("id") UUID id, @Param("search") String search, Pageable pageable);

    Optional<UserArticleLike> findByUserAndArticle(User user, Article article);
}
