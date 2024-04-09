package com.pch777.blog.article.domain.repository;

import com.pch777.blog.article.domain.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {
   Optional<Article> findByTitleUrl(String titleUrl);

   Page<Article> findByTitleContainingIgnoreCase(String title, Pageable pageable);

   Page<Article> findByCategoryId(UUID id, Pageable pageable);

   Page<Article> findByCategoryIdAndTitleContainingIgnoreCase(UUID id, String title, Pageable pageable);

    Page<Article> findByTagsId(UUID tagId, Pageable pageable);

    Page<Article> findByTagsIdAndTitleContainingIgnoreCase(UUID tagId, String trim, Pageable pageable);
}
