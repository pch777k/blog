package com.pch777.blog.tag.controller;

import com.pch777.blog.article.dto.ArticleSummaryDto;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.tag.domain.model.Tag;
import com.pch777.blog.tag.dto.TagDto;
import com.pch777.blog.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/tags")
@RequiredArgsConstructor
public class TagApiController {

    private final TagService tagService;
    private final ArticleService articleService;

    @GetMapping
    public List<Tag> getTags() {
        return tagService.getAllTags();
    }

    @GetMapping("top")
    public List<Tag> getTopTagsByPopularity() {
        return tagService.get10TagsByPopularity();
    }

    @GetMapping("{id}/articles")
    public Page<ArticleSummaryDto> getSummaryArticlesByTagId(@PathVariable UUID id,
                                                             @RequestParam(name = "field", required = false, defaultValue = "created") String field,
                                                             @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                             @RequestParam(name = "size", required = false, defaultValue = "2") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(field));

        return articleService.getSummaryArticlesByTagId(id, pageable);
    }

    @GetMapping("{id}")
    public Tag getTag(@PathVariable UUID id) {
        return tagService.getTagById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tag createTag(@Valid @RequestBody TagDto tagDto) {
        return tagService.createTag(tagDto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}


