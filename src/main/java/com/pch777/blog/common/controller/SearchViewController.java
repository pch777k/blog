package com.pch777.blog.common.controller;

import com.pch777.blog.article.dto.ArticleSummaryDto;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.article.service.UserArticleLikeService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.tag.service.TagService;
import com.pch777.blog.identity.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.pch777.blog.common.controller.ControllerUtils.paging;

@Controller
@RequestMapping("/search")
public class SearchViewController extends BlogCommonViewController {

    private final BlogConfiguration blogConfiguration;
    public SearchViewController(CategoryService categoryService,
                                ArticleService articleService,
                                TagService tagService,
                                UserService userService,
                                UserArticleLikeService userArticleLikeService,
                                BlogConfiguration blogConfiguration) {
        super(categoryService, articleService, tagService, userService, userArticleLikeService);
        this.blogConfiguration = blogConfiguration;
    }

    @GetMapping
    public String searchView(
            @RequestParam(name = "s", required = false) String search,
            @RequestParam(name = "field", required = false, defaultValue = "created") String field,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ){

        Pageable pageable = PageRequest.of(page, blogConfiguration.getArticlesPageSize(), Sort.by(field).descending());
        Page<ArticleSummaryDto> summaryArticlesPage =  articleService.getSummaryArticles(search, pageable);

        model.addAttribute("summaryArticlesPage", summaryArticlesPage);
        model.addAttribute("search", search);

        addGlobalAttributes(model, userDetails);
        paging(model, summaryArticlesPage, blogConfiguration.getArticlesPageSize());

        return "blog/search/index";
    }

}
