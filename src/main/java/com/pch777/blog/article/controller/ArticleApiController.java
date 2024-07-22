package com.pch777.blog.article.controller;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.UserArticleLike;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.dto.ArticleShortDto;
import com.pch777.blog.article.dto.ArticleSummaryDto;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.article.service.UserArticleLikeService;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.exception.UserLikedException;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("api/v1/articles")
@RestController
public class ArticleApiController {

    private final ArticleService articleService;
    private final UserService userService;
    private final UserArticleLikeService userArticleLikeService;
    private final BlogConfiguration blogConfiguration;

    @GetMapping
    public ResponseEntity<Page<ArticleSummaryDto>> getArticles(
                        @RequestParam(name = "field", required = false, defaultValue = "created") String field,
                        @RequestParam(name = "page", required = false, defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, blogConfiguration.getArticlesPageSize(), Sort.by(field).descending());
        Page<ArticleSummaryDto> articleSummaryDtoPage = articleService.getSummaryArticles(pageable);
        return ResponseEntity.ok(articleSummaryDtoPage);
    }

    @GetMapping("{titleUrl}")
    public ResponseEntity<Article> getArticle(@PathVariable String titleUrl) {
        Article article = articleService.getArticleByTitleUrl(titleUrl);
        return ResponseEntity.ok(article);
    }

    @GetMapping("top")
    public ResponseEntity<List<ArticleShortDto>> getTopArticle() {
        List<ArticleShortDto> articleShortDtoList = articleService.getTop3PopularArticles();
        return ResponseEntity.ok(articleShortDtoList);
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(@Valid @RequestBody ArticleDto articleDto, Principal principal) {
        Article createdArticle = articleService.createArticleByAuthor(articleDto, principal.getName());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdArticle.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdArticle);
    }

    @PutMapping("{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable UUID id, @Valid @RequestBody ArticleDto articleDto, Principal principal) {
        Article updatedArticle = articleService.updateArticle(id, articleDto, principal.getName());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedArticle);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable UUID id, Principal principal) {
        articleService.deleteArticle(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Object> likeArticle(@PathVariable UUID id,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access.");
        }

        User user = userService.getUserByUsername(userDetails.getUsername());
        Article article = articleService.getArticleById(id);

        try {
            UserArticleLike userArticleLike = userArticleLikeService.likeArticle(user, article);
            return ResponseEntity.ok(userArticleLike);
        } catch (UserLikedException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already liked the article.");
        }
    }

    @DeleteMapping("/{id}/unlike")
    public ResponseEntity<Void> unlikeArticle(@PathVariable UUID id,
                                              @AuthenticationPrincipal UserDetails userDetails) {

        userArticleLikeService.unlikeArticle(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

}
