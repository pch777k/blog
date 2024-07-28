package com.pch777.blog.article.controller;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.dto.ArticleAuthorPanelDto;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.service.ArticleMapper;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.common.controller.ProfileCommonViewController;
import com.pch777.blog.common.dto.Message;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.service.PrivateMessageService;
import com.pch777.blog.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

import static com.pch777.blog.common.controller.ControllerUtils.addPaginationAttributes;
import static com.pch777.blog.common.controller.ControllerUtils.paging;

@Slf4j
@Controller
@RequestMapping("author/articles")
public class ArticleAuthorViewController extends ProfileCommonViewController {

    public static final String ARTICLE_FORM = "user/author/article/form";
    public static final String MESSAGE = "message";

    public static final String REDIRECT_ARTICLES = "redirect:/articles/";
    public static final String CATEGORIES = "categories";

    private final BlogConfiguration blogConfiguration;
    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final ArticleMapper articleMapper;

    public ArticleAuthorViewController(PrivateMessageService privateMessageService,
                                       NotificationService notificationService,
                                       UserService userService,
                                       BlogConfiguration blogConfiguration,
                                       ArticleService articleService,
                                       CategoryService categoryService,
                                       ArticleMapper articleMapper) {
        super(privateMessageService, notificationService, userService);
        this.blogConfiguration = blogConfiguration;
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.articleMapper = articleMapper;
    }

    @GetMapping
    public String getArticles(
            @RequestParam(name = "s", required = false, defaultValue = "") String search,
            @RequestParam(name = "field", required = false, defaultValue = "created") String field,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.getUserByUsername(userDetails.getUsername());
        int size = blogConfiguration.getArticlesPageSize();
        Pageable pageable =
                PageRequest.of(page, size, Sort.Direction.fromString(direction), field);
        Page<ArticleAuthorPanelDto> articleSummaryPage = articleService.getArticlesSummary(user.getId(), search, pageable);
        
        addProfileAttributes(model, user);
        addPaginationAttributes(model, page, field, direction, search);
        paging(model, articleSummaryPage, size);
        model.addAttribute("summaryArticlesPage", articleSummaryPage);

        return "user/author/article/index";
    }

    @GetMapping("add")
    public String addView(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("articleDto", new ArticleDto());
        User user = userService.getUserByUsername(userDetails.getUsername());
        addProfileAttributes(model, user);
        model.addAttribute(CATEGORIES, categoryService.getCategories());

        return ARTICLE_FORM;
    }

    @PostMapping("add")
    public String add(@Valid @ModelAttribute("articleDto") ArticleDto articleDto,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes,
                      @AuthenticationPrincipal UserDetails userDetails,
                      Model model
    ) {
        if (userDetails == null) {
            model.addAttribute("error", "You must be logged in to perform this action!");
            return "redirect:/login";
        }
        User user = userService.getUserByUsername(userDetails.getUsername());
        model.addAttribute(CATEGORIES, categoryService.getCategories());
        addProfileAttributes(model, user);
        if (bindingResult.hasErrors()) {
            model.addAttribute(MESSAGE, Message.error("Error during article creation!"));

            log.error("Error on article.add");
            return ARTICLE_FORM;
        }
        Article article;
        try {
            article = articleService.createArticleByAuthor(articleDto, userDetails.getUsername());
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Article created successfully!"));
            log.info("Article created successfully!");
        } catch (Exception e) {
            log.error("Error on article.add", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during article creation!"));
            return ARTICLE_FORM;
        }

        return REDIRECT_ARTICLES + article.getTitleUrl();
    }

    @GetMapping("{id}/edit")
    public String editView(Model model, @PathVariable UUID id,
                           @AuthenticationPrincipal UserDetails userDetails) {
        ArticleDto articleDto = articleMapper.mapToArticleDto(articleService.getArticleById(id));
        User user = userService.getUserByUsername(userDetails.getUsername());
        addProfileAttributes(model, user);

        model.addAttribute("articleDto", articleDto);
        model.addAttribute("id", id);
        model.addAttribute(CATEGORIES, categoryService.getCategories());

        return ARTICLE_FORM;
    }

    @PostMapping("{id}/edit")
    public String edit(@PathVariable UUID id,
                       @Valid @ModelAttribute("articleDto") ArticleDto articleDto,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       @AuthenticationPrincipal UserDetails userDetails,
                       Model model
    ) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        addProfileAttributes(model, user);
        model.addAttribute(CATEGORIES, categoryService.getCategories());
        if (bindingResult.hasErrors()) {
            model.addAttribute(MESSAGE, Message.error("Error during article edition!"));
            log.error("Error on article.edit");
            return ARTICLE_FORM;
        }
        Article article;
        try {
            article = articleService.updateArticle(id, articleDto, userDetails.getUsername());
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Article edited successfully!"));
            log.info("Article edited successfully!");
        } catch (Exception e) {
            log.error("Error on article.edit", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during article edition!"));
            return ARTICLE_FORM;
        }

        return REDIRECT_ARTICLES + article.getTitleUrl();
    }

    @GetMapping("{id}/delete")
    public String delete(@PathVariable UUID id, @AuthenticationPrincipal UserDetails userDetails) {
        articleService.deleteArticle(id, userDetails.getUsername());
        return "redirect:/author/articles";
    }


}
