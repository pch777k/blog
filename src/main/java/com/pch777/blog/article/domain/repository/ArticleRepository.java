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
}
