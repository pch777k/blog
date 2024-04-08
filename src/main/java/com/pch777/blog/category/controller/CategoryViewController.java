package com.pch777.blog.category.controller;

import com.pch777.blog.article.dto.SummaryArticleDto;
import com.pch777.blog.article.service.ArticleMapper;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.article.service.ArticleStatsService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.common.BlogCommonViewController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.pch777.blog.common.ControllerUtils.paging;

@Controller
@RequestMapping("/categories")
public class CategoryViewController extends BlogCommonViewController {

    private final ArticleService articleService;

    public CategoryViewController(CategoryService categoryService,
                                  ArticleService articleService) {
        super(categoryService);
        this.articleService = articleService;
    }

    @GetMapping("{id}")
    public String indexView(@PathVariable UUID id,
            @RequestParam(name = "s", required = false) String search,
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

        Page<SummaryArticleDto> summaryArticlesPage =  articleService.getSummaryArticles(id, search, pageable);
        model.addAttribute("summaryArticlesPage", summaryArticlesPage);
        model.addAttribute("search", search);
        model.addAttribute("field", field);
        model.addAttribute("direction", direction);
        model.addAttribute("reverseSort", reverseSort);

        model.addAttribute("activeCategoryId", id);
        model.addAttribute("categoryName", categoryService.getCategoryById(id).getName());

        addGlobalAttributes(model);

        paging(model, summaryArticlesPage);

        return "category/index";
    }

}
