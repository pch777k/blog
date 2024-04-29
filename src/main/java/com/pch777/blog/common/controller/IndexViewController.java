package com.pch777.blog.common.controller;

import com.pch777.blog.article.dto.ArticleSummaryDto;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.tag.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.pch777.blog.common.controller.ControllerUtils.paging;


@Controller
@RequestMapping("/")
public class IndexViewController extends BlogCommonViewController {

    private final BlogConfiguration blogConfiguration;

    public IndexViewController(CategoryService categoryService,
                                 ArticleService articleService,
                                 TagService tagService,
                                 BlogConfiguration blogConfiguration) {
        super(categoryService, articleService, tagService);
        this.blogConfiguration = blogConfiguration;
    }

    @GetMapping
    public String indexView(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page,
                blogConfiguration.getArticlesPageSize(),
                Sort.by(blogConfiguration.getArticleSortField()).descending());
        Page<ArticleSummaryDto> summaryArticlesPage = articleService.getSummaryArticles(pageable);
        model.addAttribute("summaryArticlesPage", summaryArticlesPage);
        addGlobalAttributes(model);
        paging(model, summaryArticlesPage, blogConfiguration.getArticlesPageSize());

        return "index/index";
    }
}
