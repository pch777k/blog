package com.pch777.blog.common.controller;

import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.article.service.UserArticleLikeService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.tag.service.TagService;
import com.pch777.blog.identity.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

@AllArgsConstructor
public abstract class BlogCommonViewController {

    protected CategoryService categoryService;
    protected ArticleService articleService;
    protected TagService tagService;
    protected UserService userService;
    protected UserArticleLikeService userArticleLikeService;

    protected void addGlobalAttributes(Model model, UserDetails userDetails) {
        if (userDetails != null) {
            model.addAttribute("avatarUrl", userService.getUserByUsername(userDetails.getUsername()).getAvatarUrl());
            model.addAttribute("userId", userService.getUserByUsername(userDetails.getUsername()).getId());
        }
        model.addAttribute("categories", categoryService.getCategories());
        model.addAttribute("monthYearList", articleService.getNumberOfLastMonths());
        model.addAttribute("tags", tagService.get10TagsByPopularity());
        model.addAttribute("top3articles", articleService.getTop3PopularArticles());
        model.addAttribute("userArticleLikeService", userArticleLikeService);
    }
}