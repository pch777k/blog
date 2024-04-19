package com.pch777.blog.common;

import com.pch777.blog.article.dto.SummaryArticleDto;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.tag.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.pch777.blog.common.ControllerUtils.paging;

@Controller
@RequestMapping("/search")
public class SearchViewController extends BlogCommonViewController {

    public SearchViewController(CategoryService categoryService,
                                ArticleService articleService,
                                TagService tagService) {
        super(categoryService, articleService, tagService);
    }

    @GetMapping
    public String searchView(
            @RequestParam(name = "s", required = false) String search,
            @RequestParam(name = "field", required = false, defaultValue = "created") String field,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "2") int size,
            Model model
    ){

        Pageable pageable = PageRequest.of(page, size, Sort.by(field).descending());
        Page<SummaryArticleDto> summaryArticlesPage =  articleService.getSummaryArticles(search, pageable);

        model.addAttribute("summaryArticlesPage", summaryArticlesPage);
        model.addAttribute("search", search);

        addGlobalAttributes(model);
        paging(model, summaryArticlesPage, size);

        return "search/index";
    }

}
