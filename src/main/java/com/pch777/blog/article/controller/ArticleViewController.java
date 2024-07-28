package com.pch777.blog.article.controller;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.dto.ArticleSummaryDto;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.article.service.ArticleStatsService;
import com.pch777.blog.article.service.UserArticleLikeService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.comment.dto.CommentDto;
import com.pch777.blog.comment.service.CommentService;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.common.controller.BlogCommonViewController;
import com.pch777.blog.common.dto.Message;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.tag.service.TagService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.pch777.blog.common.controller.ControllerUtils.paging;

@Controller
@RequestMapping("/articles")
@Slf4j
public class ArticleViewController extends BlogCommonViewController {

    public static final String ARTICLE_SINGLE = "blog/article/single";
    public static final String REDIRECT_ARTICLES = "redirect:/articles/";
    public static final String MESSAGE = "message";
    public static final String COMMENTS_CONTAINER = "#commentsContainer";


    private final ArticleStatsService articleStatsService;
    private final CommentService commentService;
    private final BlogConfiguration blogConfiguration;


    public ArticleViewController(CategoryService categoryService,
                                 ArticleService articleService,
                                 ArticleStatsService articleStatsService,
                                 TagService tagService,
                                 CommentService commentService,
                                 UserService userService,
                                 UserArticleLikeService userArticleLikeService,
                                 BlogConfiguration blogConfiguration) {
        super(categoryService, articleService, tagService, userService, userArticleLikeService);
        this.articleStatsService = articleStatsService;
        this.commentService = commentService;
        this.blogConfiguration = blogConfiguration;
    }

    @GetMapping("{titleUrl}")
    public String singleView(@PathVariable String titleUrl,
                             @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                             Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {

        Pageable pageable = PageRequest.of(page, blogConfiguration.getCommentsPageSize(), Sort.Direction.fromString("desc"), "created");
        boolean canSendMessages = false;
        if(userDetails != null) {
            User currentUser = userService.getUserByUsername(userDetails.getUsername());
            canSendMessages = userService.canSendMessages(currentUser.getId());
        }

        prepareArticleModelAttributes(titleUrl, model, pageable);
        CommentDto commentDto = new CommentDto();
        model.addAttribute("commentDto", commentDto);
        model.addAttribute("commentService", commentService);
        model.addAttribute("canSendMessages", canSendMessages);

        addGlobalAttributes(model, userDetails);

        return ARTICLE_SINGLE;
    }

    @PostMapping("{titleUrl}/comments/add")
    public String addComment(@PathVariable String titleUrl,
                             @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                             @Valid @ModelAttribute("commentDto") CommentDto commentDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model
    ) {
        Pageable pageable = PageRequest.of(page, blogConfiguration.getCommentsPageSize(), Sort.Direction.fromString("desc"), "created");
        addGlobalAttributes(model, userDetails);
        boolean canSendMessages = false;
        if(userDetails != null) {
            User currentUser = userService.getUserByUsername(userDetails.getUsername());
            canSendMessages = userService.canSendMessages(currentUser.getId());
        }
        model.addAttribute("commentService", commentService);
        if (bindingResult.hasErrors()) {
            prepareArticleModelAttributes(titleUrl, model, pageable);
            model.addAttribute(MESSAGE, Message.error("Error during comment creation!"));
            model.addAttribute("canSendMessages", canSendMessages);
            log.error("Error on comment.add");
            return ARTICLE_SINGLE;
        }
        Article article = articleService.getArticleByTitleUrl(titleUrl);
        try {
            commentService.createComment(article.getId(), commentDto, userDetails.getUsername());
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Comment created successfully!"));
            log.info("Comment created successfully!");
        } catch (Exception e) {
            log.error("Error on comment.add", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during comment creation!"));
            model.addAttribute("canSendMessages", canSendMessages);
            return ARTICLE_SINGLE;
        }

        return REDIRECT_ARTICLES + titleUrl + COMMENTS_CONTAINER;
    }

    @GetMapping("{titleUrl}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable String titleUrl,
                                @PathVariable UUID commentId,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                Principal principal) {
        try {
            commentService.deleteComment(commentId, principal.getName());
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Comment deleted successfully!"));
            log.info("Comment deleted successfully!");
        } catch (Exception e) {
            log.error("Error on comment.delete", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during comment deletion!"));
            return ARTICLE_SINGLE;
        }

        return REDIRECT_ARTICLES + titleUrl + COMMENTS_CONTAINER;
    }

    @GetMapping("{titleUrl}/comments/{commentId}/edit")
    public String editCommentView(@PathVariable String titleUrl,
                                  @PathVariable UUID commentId,
                                  @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                  Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        Comment comment = commentService.getCommentById(commentId);
        CommentDto commentDto = new CommentDto(comment.getContent());
        Pageable pageable = PageRequest.of(page, blogConfiguration.getCommentsPageSize(), Sort.Direction.fromString("desc"), "created");
        boolean canSendMessages = false;
        if(userDetails != null) {
            User currentUser = userService.getUserByUsername(userDetails.getUsername());
            canSendMessages = userService.canSendMessages(currentUser.getId());
        }

        prepareArticleModelAttributes(titleUrl, model, pageable);

        model.addAttribute("commentDto", commentDto);
        model.addAttribute("commentService", commentService);
        model.addAttribute("canSendMessages", canSendMessages);
        model.addAttribute("commentId", commentId);

        model.addAttribute("titleUrl", titleUrl);
        addGlobalAttributes(model, userDetails);

        return ARTICLE_SINGLE;

        //return ARTICLE_COMMENT_EDIT;

        //return "blog/article/comment/form";
    }

    @PostMapping("{titleUrl}/comments/{commentId}/edit")
    public String editComment(@PathVariable String titleUrl,
                              @PathVariable UUID commentId,
                              @Valid @ModelAttribute("commentDto") CommentDto commentDto,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model,
                              @PageableDefault(size = 10) Pageable pageable) {

        addGlobalAttributes(model, userDetails);
        model.addAttribute("commentService", commentService);

        if (bindingResult.hasErrors()) {
            prepareArticleModelAttributes(titleUrl, model, pageable);
            model.addAttribute("commentDto", commentDto);
            model.addAttribute(MESSAGE, Message.error("Error during comment edition!"));
            log.error("Error on comment.edit");
            return ARTICLE_SINGLE;
        }

        try {
            commentService.updateComment(commentId, commentDto, userDetails.getUsername());
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Comment edited successfully!"));
            log.info("Comment edited successfully!");
        } catch (Exception e) {
            log.error("Error on comment.edit", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during comment edition!"));
            return ARTICLE_SINGLE;
        }

        return REDIRECT_ARTICLES + titleUrl + COMMENTS_CONTAINER;
    }

    private void prepareArticleModelAttributes(String titleUrl, Model model, Pageable pageable) {
        Article article = articleService.getArticleByTitleUrl(titleUrl);
        ArticleStats articleStats = articleStatsService.getArticleStatsByArticleId(article.getId());
        Page<Comment> commentsPage = commentService.getCommentsByArticleId(article.getId(), pageable);

        Map<UUID, Boolean> canReceiveMessagesMap = new HashMap<>();

        for (Comment comment : commentsPage.getContent()) {
            canReceiveMessagesMap.put(comment.getUser().getId(),
                    userService.canReceiveMessages(comment.getUser().getId()));
        }

        model.addAttribute("canReceiveMessageMap", canReceiveMessagesMap);
        model.addAttribute("article", article);
        model.addAttribute("articleStats", articleStats);
        model.addAttribute("articleNavigation", articleService.getArticleNavigationDto(titleUrl));
        model.addAttribute("commentsPage", commentsPage);
        paging(model, commentsPage, blogConfiguration.getArticlesPageSize());
    }

    @GetMapping("{year}/{month}")
    public String archiveIndexView(@PathVariable int year,
                                   @PathVariable String month,
                                   @RequestParam(name = "field", required = false, defaultValue = "created") String field,
                                   @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        Pageable pageable = PageRequest.of(page, blogConfiguration.getArticlesPageSize(), Sort.by(field).descending());

        Month monthEnum = Month.valueOf(month.toUpperCase());
        LocalDate monthDate = LocalDate.of(year, monthEnum, 1);

        Page<ArticleSummaryDto> summaryArticlesPage = articleService.getSummaryArticlesByMonth(monthDate, pageable);
        model.addAttribute("summaryArticlesPage", summaryArticlesPage);
        model.addAttribute("year", year);
        model.addAttribute("month", month);

        addGlobalAttributes(model, userDetails);
        paging(model, summaryArticlesPage, blogConfiguration.getArticlesPageSize());

        return "blog/article/archive/index";
    }

}
