package com.pch777.blog.identity.author.controller;

import com.pch777.blog.article.dto.ArticleSummaryDto;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.article.service.UserArticleLikeService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.common.controller.BlogCommonViewController;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.tag.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

import static com.pch777.blog.common.controller.ControllerUtils.paging;

@Controller
@RequestMapping("/authors")
public class AuthorViewController extends BlogCommonViewController {

    private final BlogConfiguration blogConfiguration;

    public AuthorViewController(CategoryService categoryService,
                                ArticleService articleService,
                                TagService tagService,
                                UserService userService,
                                UserArticleLikeService userArticleLikeService,
                                BlogConfiguration blogConfiguration) {
        super(categoryService, articleService, tagService, userService, userArticleLikeService);
        this.blogConfiguration = blogConfiguration;
    }

    @GetMapping("{authorId}/articles")
    public String indexView(@PathVariable UUID authorId,
                            @RequestParam(name = "field", required = false, defaultValue = "created") String field,
                            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                            @AuthenticationPrincipal UserDetails userDetails,
                            Model model
    ) {

        Pageable pageable = PageRequest.of(page, blogConfiguration.getArticlesPageSize(), Sort.by(field));
        User author = userService.getUserById(authorId);
        Page<ArticleSummaryDto> summaryArticlesPage = articleService.getSummaryArticlesByAuthorId(authorId, pageable);
        model.addAttribute("summaryArticlesPage", summaryArticlesPage);
        model.addAttribute("authorId", authorId);
        model.addAttribute("authorName", author.getFullName());
        model.addAttribute("top3articles", articleService.getTop3PopularArticles());

        addGlobalAttributes(model, userDetails);
        paging(model, summaryArticlesPage, blogConfiguration.getArticlesPageSize());

        return "blog/article/author/index";
    }

}
