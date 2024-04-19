package com.pch777.blog.common;

import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.tag.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;

@AllArgsConstructor
public abstract class BlogCommonViewController {

    protected CategoryService categoryService;
    protected ArticleService articleService;
    protected TagService tagService;

    protected void addGlobalAttributes(Model model) {
        model.addAttribute("categories", categoryService.getCategories());
        model.addAttribute("monthYearList", articleService.getNumberOfLastMonths());
        model.addAttribute("tags", tagService.get10TagsByPopularity());
        model.addAttribute("top3articles", articleService.getTop3PopularArticles());
    }
}