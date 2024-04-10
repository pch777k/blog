package com.pch777.blog.common;

import com.pch777.blog.article.dto.SummaryArticleDto;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.category.service.CategoryService;
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

    private final ArticleService articleService;

    public SearchViewController(CategoryService categoryService,
                                 ArticleService articleService) {
        super(categoryService);
        this.articleService = articleService;
    }

    @GetMapping
    public String searchView(
            @RequestParam(name = "s", required = false) String search,
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "field", required = false, defaultValue = "created") String field,
            @RequestParam(name = "direction", required = false, defaultValue = "desc") String direction,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "2") int size,
            Model model
    ){
        //PageRequest pageRequest = PageRequest.of(page - 1, ideasConfiguration.getPagingPageSize());
//        if(search == null || search.isBlank()) {
//            return "article/index";
//        }
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


        return "search/index";
    }

}
