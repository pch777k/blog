package com.pch777.blog.article.controller;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.dto.ShortArticleDto;
import com.pch777.blog.article.dto.SummaryArticleDto;
import com.pch777.blog.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("api/v1/articles")
@RestController
public class ArticleApiController {

    private final ArticleService articleService;

//    @GetMapping
//    public List<Article> getArticles() {
//        return articleService.getArticles();
//    }

    @GetMapping
    public Page<SummaryArticleDto> getArticles(
                        @RequestParam(name = "field", required = false, defaultValue = "created") String field,
                        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                        @RequestParam(name = "size", required = false, defaultValue = "2") int size
    ) {
        Pageable pageable = PageRequest.of(page,size, Sort.by(field).descending());
        return articleService.getSummaryArticles(pageable);
    }

    @GetMapping("{titleUrl}")
    public Article getArticle(@PathVariable String titleUrl) {
        return articleService.getArticleByTitleUrl(titleUrl);
    }

    @GetMapping("top")
    public List<ShortArticleDto> getTopArticle() {
        return articleService.getTop3PopularArticles();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Article createArticle(@RequestBody ArticleDto articleDto) {
        return articleService.createArticle(articleDto);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Article updateArticle(@PathVariable UUID id, @RequestBody ArticleDto articleDto) {
        return articleService.updateArticle(id, articleDto);
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
