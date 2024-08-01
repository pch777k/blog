package com.pch777.blog.common.controller;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

public abstract class ControllerUtils {

    private ControllerUtils() {
    }

    public static <T> void paging(Model model, Page<T> page, int size) {
        if (page != null) {
            model.addAttribute("currentPageNumber", page.getNumber());
            model.addAttribute("totalPagesNumber", page.getTotalPages());
        }
        model.addAttribute("size", size);
    }

    public static void addPaginationAttributes(Model model, int page, String field, String direction, String search) {
        model.addAttribute("currentPage", page);
        model.addAttribute("field", field);
        model.addAttribute("direction", direction);
        model.addAttribute("search", search);
    }


}
