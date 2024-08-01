package com.pch777.blog.category.controller;

import com.pch777.blog.category.domain.model.Category;
import com.pch777.blog.category.dto.CategoryDto;
import com.pch777.blog.category.service.CategoryService;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.common.controller.ProfileCommonViewController;
import com.pch777.blog.common.dto.Message;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.service.PrivateMessageService;
import com.pch777.blog.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

import static com.pch777.blog.common.controller.ControllerUtils.addPaginationAttributes;
import static com.pch777.blog.common.controller.ControllerUtils.paging;

@Controller
@RequestMapping("/admin/categories")
@Slf4j
public class CategoryAdminViewController extends ProfileCommonViewController {

    public static final String CATEGORY_FORM = "admin/category/form";
    public static final String REDIRECT_ADMIN_CATEGORIES = "redirect:/admin/categories";
    public static final String MESSAGE = "message";
    public static final String CATEGORY_DTO = "categoryDto";
    private final CategoryService categoryService;
    private final BlogConfiguration blogConfiguration;

    public CategoryAdminViewController(PrivateMessageService privateMessageService,
                                       NotificationService notificationService,
                                       UserService userService,
                                       CategoryService categoryService,
                                       BlogConfiguration blogConfiguration) {
        super(privateMessageService, notificationService, userService);
        this.categoryService = categoryService;
        this.blogConfiguration = blogConfiguration;
    }

    @GetMapping
    public String indexView(
            @RequestParam(name = "s", required = false, defaultValue = "") String search,
            @RequestParam(name = "field", required = false, defaultValue = "id") String field,
            @RequestParam(name = "direction", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        User user = userService.getUserByUsername(userDetails.getUsername());
        int size = blogConfiguration.getPageSize();
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(direction), field);

        Page<Category> categoryPage = categoryService.getCategories(search, pageable);
        model.addAttribute("categoryPage", categoryPage);

        addProfileAttributes(model, user);
        addPaginationAttributes(model, page, field, direction, search);
        paging(model, categoryPage, size);

        return "admin/category/index";
    }

    @GetMapping("add")
    public String addView(Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        model.addAttribute(CATEGORY_DTO, new CategoryDto());
        addProfileAttributes(model, user);

        return CATEGORY_FORM;
    }

    @PostMapping("add")
    public String addCategory(@Valid @ModelAttribute(CATEGORY_DTO) CategoryDto categoryDto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes ra,
                          @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        addProfileAttributes(model, user);
        if (result.hasErrors()) {
            log.warn("Validation errors while adding category: {}",  result.getAllErrors());
            model.addAttribute(CATEGORY_DTO, categoryDto);
            model.addAttribute(MESSAGE, Message.error("Error during category addition!"));
            return CATEGORY_FORM;
        }

        if (categoryService.isCategoryExists(categoryDto.getName())) {
            log.warn("Attempt to add category with duplicate name '{}'", categoryDto.getName());
            result.rejectValue("name", "error.categoryDto", "Category with this name already exists.");
            model.addAttribute(CATEGORY_DTO, categoryDto);
            model.addAttribute(MESSAGE, Message.error("Error during category addition!"));

            return CATEGORY_FORM;
        }

        try {
            categoryService.createCategory(categoryDto);
            ra.addFlashAttribute(MESSAGE, Message.info("Category created successfully."));
            log.info("Category with name {} was successfully created by user {}", categoryDto.getName(), user.getUsername());

        } catch (Exception e) {
            log.error("Error on category.add", e);
            model.addAttribute(CATEGORY_DTO, categoryDto);
            model.addAttribute(MESSAGE, Message.error("Unknown error during category addition!"));
            return CATEGORY_FORM;
        }

        return REDIRECT_ADMIN_CATEGORIES;
    }

    @GetMapping("{id}/edit")
    public String editView(Model model,
                           @PathVariable UUID id,
                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Category category = categoryService.getCategoryById(id);
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName(category.getName());
        model.addAttribute(CATEGORY_DTO, categoryDto);
        model.addAttribute("id", id);
        addProfileAttributes(model, user);

        return CATEGORY_FORM;
    }

    @PostMapping("{id}/edit")
    public String editCategory(@PathVariable("id") UUID id,
                          @Valid @ModelAttribute(CATEGORY_DTO) CategoryDto categoryDto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes ra,
                          @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        addProfileAttributes(model, user);
        if (result.hasErrors()) {
            log.warn("Validation errors while editing category with ID {}: {}", id, result.getAllErrors());
            model.addAttribute(CATEGORY_DTO, categoryDto);
            model.addAttribute("id", id);
            model.addAttribute(MESSAGE, Message.error("Error during category edition!"));

            return CATEGORY_FORM;
        }

        if (categoryService.isCategoryExists(categoryDto.getName())) {
            log.warn("Attempt to edit category with duplicate name '{}'", categoryDto.getName());
            result.rejectValue("name", "error.categoryDto", "Category with this name already exists.");
            model.addAttribute("id", id);
            model.addAttribute(CATEGORY_DTO, categoryDto);
            model.addAttribute(MESSAGE, Message.error("Error during category edition!"));
            return CATEGORY_FORM;
        }

        try {
            categoryService.updateCategory(id, categoryDto);
            ra.addFlashAttribute(MESSAGE, Message.info("Category edited successfully."));
            log.info("Category with ID {} was successfully edited by user {}", id, user.getUsername());

        } catch (Exception e) {
            log.error("Error on category.edit", e);
            model.addAttribute(CATEGORY_DTO, categoryDto);
            model.addAttribute("id", id);
            model.addAttribute(MESSAGE, Message.error("Unknown error during category edition!"));
            return CATEGORY_FORM;
        }

        return REDIRECT_ADMIN_CATEGORIES;
    }

    @GetMapping("{id}/delete")
    public String deleteView(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            categoryService.deleteCategory(id);
            ra.addFlashAttribute(MESSAGE, Message.info("Category deleted successfully."));
        } catch (Exception e) {
            log.error("Error on category.delete", e);
            ra.addFlashAttribute(MESSAGE, Message.error("Unknown error during category deletion!"));
            return REDIRECT_ADMIN_CATEGORIES;
        }

        return REDIRECT_ADMIN_CATEGORIES;
    }
}

