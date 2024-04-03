package com.pch777.blog.article.controller;

import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.service.ArticleMapper;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.common.BlogCommonViewController;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/articles")
public class ArticleViewController extends BlogCommonViewController {

    public static final String ARTICLE_ADD = "article/add";
    public static final String ARTICLE_EDIT = "article/edit";
    public static final String REDIRECT_ARTICLES = "redirect:/articles";
    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    public ArticleViewController(CategoryService categoryService,
                                 ArticleService articleService,
                                 ArticleMapper articleMapper) {
        super(categoryService);
        this.articleService = articleService;
        this.articleMapper = articleMapper;
    }

    @GetMapping("template")
    public String templateView() {
        return "template";
    }

    @GetMapping
    public String indexView(Model model) {
        model.addAttribute("articles", articleService.getArticles());
        addGlobalAttributes(model);
        return "article/index";
    }

    @GetMapping("{id}")
    public String singleView(Model model, @PathVariable UUID id) {
        model.addAttribute("article", articleService.getArticleById(id));
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
            addGlobalAttributes(model);
            return ARTICLE_ADD;
        }

        return REDIRECT_ARTICLES;
    }

    @GetMapping("{id}/edit")
    public String editView(Model model, @PathVariable UUID id) {
        ArticleDto articleDto = articleMapper.map(articleService.getArticleById(id));
        model.addAttribute("articleDto", articleDto);
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

}
