package com.pch777.blog.article.controller;

import com.pch777.blog.article.dto.ArticleSummaryDto;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.common.controller.ProfileCommonViewController;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.service.PrivateMessageService;
import com.pch777.blog.notification.service.NotificationService;
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

import static com.pch777.blog.common.controller.ControllerUtils.addPaginationAttributes;
import static com.pch777.blog.common.controller.ControllerUtils.paging;

@Controller
@RequestMapping("/admin/articles")
public class ArticleAdminViewController extends ProfileCommonViewController {

    private final ArticleService articleService;
    private final BlogConfiguration blogConfiguration;

    public ArticleAdminViewController(PrivateMessageService privateMessageService,
                                      NotificationService notificationService,
                                      UserService userService,
                                      ArticleService articleService,
                                      BlogConfiguration blogConfiguration) {
        super(privateMessageService, notificationService, userService);
        this.articleService = articleService;
        this.blogConfiguration = blogConfiguration;
    }

    @GetMapping
    public String indexView(
            @RequestParam(name = "s", required = false) String search,
            @RequestParam(name = "field", required = false, defaultValue = "created") String field,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        int size = blogConfiguration.getCommentsPageSize();
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), field);

        Page<ArticleSummaryDto> summaryArticlesPage = articleService.getSummaryArticles(search, pageable);

        addProfileAttributes(model, user);
        paging(model, summaryArticlesPage, blogConfiguration.getArticlesPageSize());
        addPaginationAttributes(model, page, field, direction, search);
        model.addAttribute("summaryArticlesPage", summaryArticlesPage);

        return "admin/article/index";
    }

}

