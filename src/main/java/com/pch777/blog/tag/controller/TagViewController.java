package com.pch777.blog.tag.controller;

import com.pch777.blog.article.dto.SummaryArticleDto;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.common.BlogCommonViewController;
import com.pch777.blog.tag.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

import static com.pch777.blog.common.ControllerUtils.paging;

@Controller
@RequestMapping("/tags")
public class TagViewController extends BlogCommonViewController {

    private final ArticleService articleService;
    private final TagService tagService;

    public TagViewController(CategoryService categoryService,
                             ArticleService articleService,
                             TagService tagService) {
        super(categoryService);
        this.articleService = articleService;
        this.tagService = tagService;
    }

    @GetMapping("{id}")
    public String indexView(@PathVariable UUID id,
            @RequestParam(name = "field", required = false, defaultValue = "created") String field,
            @RequestParam(name = "direction", required = false, defaultValue = "desc") String direction,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "2") int size,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), field);

        String reverseSort = null;
        if ("asc".equals(direction)) {
            reverseSort = "desc";
        } else {
            reverseSort = "asc";
        }

        Page<SummaryArticleDto> summaryArticlesPage =  articleService.getSummaryArticlesByTagId(id, pageable);
        model.addAttribute("summaryArticlesPage", summaryArticlesPage);
        model.addAttribute("field", field);
        model.addAttribute("direction", direction);
        model.addAttribute("reverseSort", reverseSort);

        model.addAttribute("tagId", id);
        model.addAttribute("tagName", tagService.getTagById(id).getName());

        addGlobalAttributes(model);

        paging(model, summaryArticlesPage);

        return "tag/index";
    }

}