package com.pch777.blog.article.controller;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.dto.ArticleShortDto;
import com.pch777.blog.article.dto.ArticleSummaryDto;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.common.configuration.BlogConfiguration;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("api/v1/articles")
@RestController
public class ArticleApiController {

    private final ArticleService articleService;
    private final BlogConfiguration blogConfiguration;

    @GetMapping
    public Page<ArticleSummaryDto> getArticles(
                        @RequestParam(name = "field", required = false, defaultValue = "created") String field,
                        @RequestParam(name = "page", required = false, defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, blogConfiguration.getArticlesPageSize(), Sort.by(field).descending());
        return articleService.getSummaryArticles(pageable);
    }

    @GetMapping("{titleUrl}")
    public Article getArticle(@PathVariable String titleUrl) {
        return articleService.getArticleByTitleUrl(titleUrl);
    }

    @GetMapping("top")
    public List<ArticleShortDto> getTopArticle() {
        return articleService.getTop3PopularArticles();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Article createArticle(@Valid @RequestBody ArticleDto articleDto, Principal principal) {
        return articleService.createArticleByAuthor(articleDto, principal.getName());
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Article updateArticle(@PathVariable UUID id, @Valid @RequestBody ArticleDto articleDto, Principal principal) {
        return articleService.updateArticle(id, articleDto, principal.getName());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticle(@PathVariable UUID id) {
        articleService.deleteArticle(id);
    }

    @PostMapping("{id}/like")
    @ResponseStatus(HttpStatus.OK)
    public void like(@PathVariable UUID id) {
        articleService.increaseLikes(id);
    }
}
