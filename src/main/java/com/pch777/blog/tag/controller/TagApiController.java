package com.pch777.blog.tag.controller;

import com.pch777.blog.article.dto.SummaryArticleDto;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.tag.domain.model.Tag;
import com.pch777.blog.tag.dto.TagDto;
import com.pch777.blog.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
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
    public Page<SummaryArticleDto> getSummaryArticlesByTagId(@PathVariable UUID id,
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
    public Tag createTag(@RequestBody TagDto tagDto) {
        return tagService.createTag(tagDto);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Tag updateTag(@PathVariable UUID id, @RequestBody TagDto tagDto) {
        return tagService.updateTag(id, tagDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
    }
}


