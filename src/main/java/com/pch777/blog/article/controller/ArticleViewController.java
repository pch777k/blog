package com.pch777.blog.article.controller;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.dto.ArticleSummaryDto;
import com.pch777.blog.article.service.ArticleMapper;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.article.service.ArticleStatsService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.comment.dto.CommentDto;
import com.pch777.blog.comment.service.CommentService;
import com.pch777.blog.common.controller.BlogCommonViewController;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.common.dto.Message;
import com.pch777.blog.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

import static com.pch777.blog.common.controller.ControllerUtils.paging;

@Controller
@RequestMapping("/articles")
@Slf4j
public class ArticleViewController extends BlogCommonViewController {

    public static final String ARTICLE_ADD = "article/add";
    public static final String ARTICLE_EDIT = "article/edit";
    public static final String ARTICLE_SINGLE = "article/single";
    public static final String REDIRECT_ARTICLES = "redirect:/articles/";
    public static final String MESSAGE = "message";
    public static final String ARTICLE_COMMENT_EDIT = "article/comment/edit";
    public static final String COMMENTS_CONTAINER = "#commentsContainer";


    private final ArticleStatsService articleStatsService;
    private final CommentService commentService;
    private final ArticleMapper articleMapper;

    private final BlogConfiguration blogConfiguration;

    public ArticleViewController(CategoryService categoryService,
                                 ArticleService articleService,
                                 ArticleStatsService articleStatsService,
                                 ArticleMapper articleMapper,
                                 TagService tagService,
                                 CommentService commentService,
                                 BlogConfiguration blogConfiguration) {
        super(categoryService, articleService, tagService);
        this.articleStatsService = articleStatsService;
        this.articleMapper = articleMapper;
        this.commentService = commentService;
        this.blogConfiguration = blogConfiguration;
    }

    @GetMapping("template")
    public String templateView() {
        return "template";
    }

    @GetMapping("{titleUrl}")
    public String singleView(@PathVariable String titleUrl,
                             @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                             Model model) {

        Pageable pageable = PageRequest.of(page, blogConfiguration.getCommentsPageSize());

        prepareArticleModelAttributes(titleUrl, model, pageable);
        CommentDto commentDto = new CommentDto();
        model.addAttribute("commentDto", commentDto);

        addGlobalAttributes(model);

        return ARTICLE_SINGLE;
    }

    @PostMapping("{titleUrl}/comments/add")
    public String addComment(@PathVariable String titleUrl,
                             @Valid @ModelAttribute("commentDto") CommentDto commentDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Principal principal,
                             Model model,
                             @PageableDefault(size = 2) Pageable pageable
    ) {
        addGlobalAttributes(model);
        if (bindingResult.hasErrors()) {
            prepareArticleModelAttributes(titleUrl, model, pageable);
            model.addAttribute(MESSAGE, Message.error("Error during comment creation!"));
            log.error("Error on comment.add");
            return ARTICLE_SINGLE;
        }
        Article article = articleService.getArticleByTitleUrl(titleUrl);
        try {
            commentService.createComment(article.getId(), commentDto, principal.getName());
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Comment created successfully!"));
            log.info("Comment created successfully!");
        } catch (Exception e) {
            log.error("Error on comment.add", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during comment creation!"));
            return ARTICLE_SINGLE;
        }

        return REDIRECT_ARTICLES + titleUrl + COMMENTS_CONTAINER;
    }
  //  @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @GetMapping("{titleUrl}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable String titleUrl,
                                @PathVariable UUID commentId,
                                Principal principal) {
        commentService.deleteComment(commentId, principal.getName());
        return REDIRECT_ARTICLES + titleUrl + COMMENTS_CONTAINER;
    }
    @GetMapping("{titleUrl}/comments/{commentId}/edit")
    public String editCommentView(@PathVariable String titleUrl,
                                  @PathVariable UUID commentId,
                                  Model model) {
        Comment comment = commentService.getCommentById(commentId);
        CommentDto commentDto = new CommentDto(comment.getContent());
        model.addAttribute("titleUrl", titleUrl);
        model.addAttribute("commentDto", commentDto);


        return ARTICLE_COMMENT_EDIT;
    }


    @PostMapping("{titleUrl}/comments/{commentId}/edit")
    public String editComment(@PathVariable String titleUrl,
                              @PathVariable UUID commentId,
                              @Valid @ModelAttribute("commentDto") CommentDto commentDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Principal principal,
                              Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(MESSAGE, Message.error("Error during comment edition!"));
            log.error("Error on comment.edit");
            return ARTICLE_COMMENT_EDIT;
        }

        try {
            commentService.updateComment(commentId, commentDto, principal.getName());
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Comment edited successfully!"));
            log.info("Comment edited successfully!");
        } catch (Exception e) {
            log.error("Error on comment.edit", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during comment edition!"));
            return ARTICLE_COMMENT_EDIT;
        }

        return REDIRECT_ARTICLES + titleUrl + COMMENTS_CONTAINER;
    }

    private void prepareArticleModelAttributes(String titleUrl, Model model, Pageable pageable) {
        Article article = articleService.getArticleByTitleUrl(titleUrl);
        ArticleStats articleStats = articleStatsService.getArticleStatsByArticleId(article.getId());
        Page<Comment> commentsPage = commentService.getCommentsByArticleId(article.getId(), pageable);

        model.addAttribute("article", article);
        model.addAttribute("articleStats", articleStats);
        model.addAttribute("articleNavigation", articleService.getArticleNavigationDto(titleUrl));
        model.addAttribute("commentsPage", commentsPage);
        paging(model, commentsPage, blogConfiguration.getArticlesPageSize());
    }

   // @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @GetMapping
    public String addView(Model model) {
        model.addAttribute("articleDto", new ArticleDto());
        addGlobalAttributes(model);
        return ARTICLE_ADD;
    }

   // @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @PostMapping
    public String add(@Valid @ModelAttribute("articleDto") ArticleDto articleDto,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes,
                      Principal principal,
                      Model model
    ) {
        if (principal == null) {
            model.addAttribute("error", "You must be logged in to perform this action!");
            return "redirect:/login";
        }

        addGlobalAttributes(model);
        if (bindingResult.hasErrors()) {
            model.addAttribute(MESSAGE, Message.error("Error during article creation!"));
            log.error("Error on article.add");
            return ARTICLE_ADD;
        }
        Article article;
        try {
            article = articleService.createArticleByAuthor(articleDto, principal.getName());
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Article created successfully!"));
            log.info("Article created successfully!");
        } catch (Exception e) {
            log.error("Error on article.add", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during article creation!"));
            return ARTICLE_ADD;
        }

        return REDIRECT_ARTICLES + article.getTitleUrl();
    }

   // @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @GetMapping("{id}/edit")
    public String editView(Model model, @PathVariable UUID id) {
        ArticleDto articleDto = articleMapper.mapToArticleDto(articleService.getArticleById(id));
        model.addAttribute("articleDto", articleDto);
        model.addAttribute("id", id);
        addGlobalAttributes(model);

        return ARTICLE_EDIT;
    }

    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @PostMapping("{id}/edit")
    public String edit(@PathVariable UUID id,
                       @Valid @ModelAttribute("articleDto") ArticleDto articleDto,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       Principal principal,
                       Model model
    ) {
        addGlobalAttributes(model);
        if (bindingResult.hasErrors()) {
            model.addAttribute(MESSAGE, Message.error("Error during article edition!"));
            log.error("Error on article.edit");
            return ARTICLE_EDIT;
        }
        Article article;
        try {
            article = articleService.updateArticle(id, articleDto, principal.getName());
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Article edited successfully!"));
            log.info("Article edited successfully!");
        } catch (Exception e) {
            log.error("Error on article.edit", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during article edition!"));
            return ARTICLE_EDIT;
        }

        return REDIRECT_ARTICLES + article.getTitleUrl();
    }

    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    @GetMapping("{id}/delete")
    public String delete(@PathVariable UUID id) {
        articleService.deleteArticle(id);
        return "redirect:/articles";
    }

    @GetMapping("{year}/{month}")
    public String archiveIndexView(@PathVariable int year,
                                   @PathVariable String month,
                                   @RequestParam(name = "field", required = false, defaultValue = "created") String field,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                   Model model) {
        Pageable pageable = PageRequest.of(page, blogConfiguration.getArticlesPageSize(), Sort.by(field).descending());

        Month monthEnum = Month.valueOf(month.toUpperCase());
        LocalDate monthDate = LocalDate.of(year, monthEnum, 1);

        Page<ArticleSummaryDto> summaryArticlesPage = articleService.getSummaryArticlesByMonth(monthDate, pageable);
        model.addAttribute("summaryArticlesPage", summaryArticlesPage);
        model.addAttribute("year", year);
        model.addAttribute("month", month);

        addGlobalAttributes(model);
        paging(model, summaryArticlesPage, blogConfiguration.getArticlesPageSize());

        return "article/archive/index";
    }

}
