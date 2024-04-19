package com.pch777.blog.article.controller;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.dto.SummaryArticleDto;
import com.pch777.blog.article.service.ArticleMapper;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.article.service.ArticleStatsService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.comment.dto.CommentDto;
import com.pch777.blog.comment.service.CommentService;
import com.pch777.blog.common.BlogCommonViewController;
import com.pch777.blog.common.dto.Message;
import com.pch777.blog.tag.service.TagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.pch777.blog.common.ControllerUtils.paging;

@Controller
@RequestMapping("/articles")
@Slf4j
public class ArticleViewController extends BlogCommonViewController {

    public static final String ARTICLE_ADD = "article/add";
    public static final String ARTICLE_EDIT = "article/edit";
    public static final String MESSAGE = "message";


    private final ArticleStatsService articleStatsService;
    private final CommentService commentService;
    private final ArticleMapper articleMapper;

    public ArticleViewController(CategoryService categoryService,
                                 ArticleService articleService,
                                 ArticleStatsService articleStatsService,
                                 ArticleMapper articleMapper,
                                 TagService tagService,
                                 CommentService commentService) {
        super(categoryService, articleService, tagService);
        this.articleStatsService = articleStatsService;
        this.articleMapper = articleMapper;
        this.commentService = commentService;
    }

    @GetMapping("template")
    public String templateView() {
        return "template";
    }

    @GetMapping
    public String indexView(
            @RequestParam(name = "field", required = false, defaultValue = "created") String field,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "2") int size,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(field).descending());
        Page<SummaryArticleDto> summaryArticlesPage =  articleService.getSummaryArticles(pageable);
        model.addAttribute("summaryArticlesPage", summaryArticlesPage);
        addGlobalAttributes(model);
        paging(model, summaryArticlesPage, size);

        return "article/index";
    }

    @GetMapping("{titleUrl}")
    public String singleView(Model model, @PathVariable String titleUrl, @PageableDefault(size = 2) Pageable pageable) {
        Article article = articleService.getArticleByTitleUrl(titleUrl);
        ArticleStats articleStats = articleStatsService.getArticleStatsByArticleId(article.getId());

        Page<Comment> commentsPage = commentService.getCommentsByArticleId(article.getId(), pageable);

        model.addAttribute("article", article);
        model.addAttribute("articleStats", articleStats);
        model.addAttribute("articleNavigation", articleService.getArticleNavigationDto(titleUrl));
        model.addAttribute("commentsPage", commentsPage);
        CommentDto commentDto = new CommentDto();
        model.addAttribute("commentDto", commentDto);
        addGlobalAttributes(model);

        return "article/single";
    }

    @PostMapping("{titleUrl}/comments/add")
    public String addComment(@PathVariable String titleUrl,
                             @Valid @ModelAttribute("commentDto") CommentDto commentDto,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes,
                      Model model, @PageableDefault(size = 2) Pageable pageable
    ) {

        if (bindingResult.hasErrors()) {
            Article article = articleService.getArticleByTitleUrl(titleUrl);
            ArticleStats articleStats = articleStatsService.getArticleStatsByArticleId(article.getId());

            Page<Comment> commentsPage = commentService.getCommentsByArticleId(article.getId(), pageable);

            model.addAttribute("article", article);
            model.addAttribute("articleStats", articleStats);
            model.addAttribute("articleNavigation", articleService.getArticleNavigationDto(titleUrl));
            model.addAttribute("commentsPage", commentsPage);
            addGlobalAttributes(model);
            model.addAttribute(MESSAGE, Message.error("Error during comment creation!"));
            log.error("Error on comment.add");
            return "article/single";
        }
        Article article = articleService.getArticleByTitleUrl(titleUrl);
        try {
            commentService.createComment(article.getId(), commentDto);
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Comment created successfully!"));
            log.info("Comment created successfully!");
        } catch (Exception e) {
            log.error("Error on comment.add", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during comment creation!"));
            addGlobalAttributes(model);
            return "article/single";
        }

        return "redirect:/articles/" + article.getTitleUrl() + "#commentsContainer";
    }

//    @GetMapping("{titleUrl}")
//    public String singleView(Model model, @PathVariable String titleUrl) {
//        Article article = articleService.getArticleByTitleUrl(titleUrl);
//        ArticleStats articleStats = articleStatsService.getArticleStatsByArticleId(article.getId());
//
//
//
//        model.addAttribute("article", article);
//        model.addAttribute("articleStats", articleStats);
//        model.addAttribute("articleNavigation", articleService.getArticleNavigationDto(titleUrl));
//        model.addAttribute("commentsPage", commentsPage); // Dodaj komentarze do modelu
//        CommentDto commentDto = new CommentDto();
//        model.addAttribute("commentDto", commentDto);
//        addGlobalAttributes(model);
//
//        return "article/single";
//    }


    @GetMapping("add")
    public String addView(Model model) {
        model.addAttribute("articleDto", new ArticleDto());
        addGlobalAttributes(model);
        return ARTICLE_ADD;
    }

    @PostMapping
    public String add(@Valid @ModelAttribute("articleDto") ArticleDto articleDto,
                      BindingResult bindingResult,
                      RedirectAttributes redirectAttributes,
                      Model model
    ) {

        if (bindingResult.hasErrors()) {
            addGlobalAttributes(model);
            model.addAttribute(MESSAGE, Message.error("Error during article creation!"));
            log.error("Error on article.add");
            return ARTICLE_ADD;
        }
        Article article;
        try {
            article = articleService.createArticle(articleDto);
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Article created successfully!"));
            log.info("Article created successfully!");
        } catch (Exception e) {
            log.error("Error on article.add", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during article creation!"));
            addGlobalAttributes(model);
            return ARTICLE_ADD;
        }

        return "redirect:/articles/" + article.getTitleUrl();
    }

    @GetMapping("{id}/edit")
    public String editView(Model model, @PathVariable UUID id) {
        ArticleDto articleDto = articleMapper.mapToArticleDto(articleService.getArticleById(id));
        model.addAttribute("articleDto", articleDto);
        model.addAttribute("id", id);
        addGlobalAttributes(model);

        return ARTICLE_EDIT;
    }

    @PostMapping("{id}/edit")
    public String edit(@PathVariable UUID id,
                       @Valid @ModelAttribute("articleDto") ArticleDto articleDto,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       Model model
    ) {

        if (bindingResult.hasErrors()) {
            addGlobalAttributes(model);
            model.addAttribute(MESSAGE, Message.error("Error during article edition!"));
            log.error("Error on article.edit");
            return ARTICLE_EDIT;
        }
        Article article;
        try {
            article = articleService.updateArticle(id, articleDto);
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("Article edited successfully!"));
            log.info("Article edited successfully!");
        } catch (Exception e) {
            log.error("Error on article.edit", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during article edition!"));
            addGlobalAttributes(model);
            return ARTICLE_EDIT;
        }

        return "redirect:/articles/" + article.getTitleUrl();
    }

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
                                   @RequestParam(name = "size", required = false, defaultValue = "2") int size,
                                   Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(field).descending());

        Month monthEnum = Month.valueOf(month.toUpperCase());
        LocalDate monthDate = LocalDate.of(year, monthEnum, 1);

        Page<SummaryArticleDto> summaryArticlesPage =  articleService.getSummaryArticlesByMonth(monthDate, pageable);
        model.addAttribute("summaryArticlesPage", summaryArticlesPage);
        model.addAttribute("year", year);
        model.addAttribute("month", month);

        addGlobalAttributes(model);

        paging(model, summaryArticlesPage, size);

        return "article/archive/index";
    }

}
