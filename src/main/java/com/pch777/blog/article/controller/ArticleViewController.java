package com.pch777.blog.article.controller;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.dto.SummaryArticleDto;
import com.pch777.blog.article.service.ArticleMapper;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.article.service.ArticleStatsService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.common.BlogCommonViewController;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.UUID;

import static com.pch777.blog.common.ControllerUtils.paging;

@Controller
@RequestMapping("/articles")
public class ArticleViewController extends BlogCommonViewController {

    public static final String ARTICLE_ADD = "article/add";
    public static final String ARTICLE_EDIT = "article/edit";
    public static final String REDIRECT_ARTICLES = "redirect:/articles";

    private final ArticleService articleService;
    private final ArticleStatsService articleStatsService;
    private final ArticleMapper articleMapper;



    public ArticleViewController(CategoryService categoryService,
                                 ArticleService articleService,
                                 ArticleStatsService articleStatsService,
                                 ArticleMapper articleMapper) {
        super(categoryService);
        this.articleService = articleService;
        this.articleStatsService = articleStatsService;
        this.articleMapper = articleMapper;
    }

    @GetMapping("template")
    public String templateView() {
        return "template";
    }

    @GetMapping
    public String indexView(
            @RequestParam(name = "s", required = false) String search,
            @RequestParam(name = "field", required = false, defaultValue = "created") String field,
            @RequestParam(name = "direction", required = false, defaultValue = "desc") String direction,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "2") int size,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), field);

        String reverseSort = null;
        if ("asc".equals(direction)) {
            reverseSort = "desc";
        } else {
            reverseSort = "asc";
        }

        Page<SummaryArticleDto> summaryArticlesPage =  articleService.getSummaryArticles(search, pageable);
        model.addAttribute("summaryArticlesPage", summaryArticlesPage);
        model.addAttribute("search", search);
        model.addAttribute("field", field);
        model.addAttribute("direction", direction);
        model.addAttribute("reverseSort", reverseSort);

        addGlobalAttributes(model);

        paging(model, summaryArticlesPage);

        return "article/index";
    }

    @GetMapping("{titleUrl}")
    public String singleView(Model model, @PathVariable String titleUrl) {
        Article article = articleService.getArticleByTitleUrl(titleUrl);
        ArticleStats articleStats = articleStatsService.getArticleStatsByArticleId(article.getId());
        model.addAttribute("article", article);
        model.addAttribute("articleStats", articleStats);
        addGlobalAttributes(model);

        return "article/single";
    }

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
            model.addAttribute("message", "Error during article creation");
            return ARTICLE_ADD;
        }

        try {
            articleService.createArticle(articleDto);
            //redirectAttributes.addFlashAttribute("message", "Article created");
        } catch (Exception e) {
            model.addAttribute("message", "Unknown error during article creation");
            e.printStackTrace();
            addGlobalAttributes(model);
            return ARTICLE_ADD;
        }

        return REDIRECT_ARTICLES;
    }

    @GetMapping("{id}/edit")
    public String editView(Model model, @PathVariable UUID id) {
        ArticleDto articleDto = articleMapper.map(articleService.getArticleById(id));
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
            model.addAttribute("message", "Error during article edition");
            return ARTICLE_EDIT;
        }

        try {
            articleService.updateArticle(id, articleDto);
            //redirectAttributes.addFlashAttribute("message", Message.info("Article edited"));
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Unknown during article edition");
            addGlobalAttributes(model);
            return ARTICLE_EDIT;
        }

        return REDIRECT_ARTICLES;
    }

    @GetMapping("{id}/delete")
    public String delete(@PathVariable UUID id) {
        articleService.deleteArticle(id);
        return REDIRECT_ARTICLES;
    }

    @PostMapping("{id}/like")
    public String like(@PathVariable UUID id, Model model) {
        articleService.increaseLikes(id);
        model.addAttribute("articles", articleService.getArticles());
        addGlobalAttributes(model);
        return REDIRECT_ARTICLES;
    }

}
