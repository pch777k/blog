package com.pch777.blog.category.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.pch777.blog.common.ControllerUtils.paging;

@Controller
@RequestMapping("/categories")
public class CategoryViewController extends BlogCommonViewController {

    public CategoryViewController(CategoryService categoryService,
                                  ArticleService articleService,
                                  TagService tagService) {
        super(categoryService, articleService, tagService);
    }

    @GetMapping("{id}")
    public String indexView(@PathVariable UUID id,
            @RequestParam(name = "field", required = false, defaultValue = "created") String field,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "2") int size,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(field));

        Page<SummaryArticleDto> summaryArticlesPage =  articleService.getSummaryArticlesByCategoryId(id, pageable);
        model.addAttribute("summaryArticlesPage", summaryArticlesPage);
        model.addAttribute("activeCategoryId", id);
        model.addAttribute("categoryName", categoryService.getCategoryById(id).getName());
        model.addAttribute("top3articles", articleService.getTop3PopularArticles());

        addGlobalAttributes(model);
        paging(model, summaryArticlesPage, size);

        return "category/index";
    }

}
