package com.pch777.blog.tag.controller;

import com.pch777.blog.article.dto.ArticleSummaryDto;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.common.controller.BlogCommonViewController;
import com.pch777.blog.common.configuration.BlogConfiguration;
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

import static com.pch777.blog.common.controller.ControllerUtils.paging;

@Controller
@RequestMapping("/tags")
public class TagViewController extends BlogCommonViewController {
    private final BlogConfiguration blogConfiguration;

    public TagViewController(CategoryService categoryService,
                             ArticleService articleService,
                             TagService tagService,
                             BlogConfiguration blogConfiguration) {
        super(categoryService, articleService, tagService);
        this.blogConfiguration = blogConfiguration;
        this.tagService = tagService;
    }

    @GetMapping("{name}/articles")
    public String indexView(@PathVariable String name,
            @RequestParam(name = "field", required = false, defaultValue = "created") String field,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page, blogConfiguration.getArticlesPageSize(), Sort.by(field));

        Page<ArticleSummaryDto> summaryArticlesPage =  articleService.getSummaryArticlesByTagName(name, pageable);
        model.addAttribute("summaryArticlesPage", summaryArticlesPage);
        model.addAttribute("tagName", tagService.getTagByName(name).getName());

        addGlobalAttributes(model);
        paging(model, summaryArticlesPage, blogConfiguration.getArticlesPageSize());

        return "tag/index";
    }

}
