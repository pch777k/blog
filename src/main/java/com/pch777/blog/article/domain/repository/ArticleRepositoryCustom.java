package com.pch777.blog.article.domain.repository;

import com.pch777.blog.article.dto.ArticleAuthorPanelDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArticleRepositoryCustom {
    Page<ArticleAuthorPanelDto> findSummaryByAuthorIdAndTitle(UUID authorId, String title, Pageable pageable);
}
