package com.pch777.blog.common;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

public abstract class ControllerUtils {

    public static <T> void paging(Model model, Page<T> page, int size) {
        if (page != null) {
            model.addAttribute("currentPageNumber", page.getNumber());
            model.addAttribute("totalPagesNumber", page.getTotalPages());
        }
        model.addAttribute("size", size);
    }
}
