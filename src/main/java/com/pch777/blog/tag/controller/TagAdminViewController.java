package com.pch777.blog.tag.controller;

import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.common.controller.ProfileCommonViewController;
import com.pch777.blog.common.dto.Message;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.message.service.PrivateMessageService;
import com.pch777.blog.notification.service.NotificationService;
import com.pch777.blog.tag.domain.model.Tag;
import com.pch777.blog.tag.dto.TagUpdateDto;
import com.pch777.blog.tag.service.TagService;
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
@RequestMapping("/admin/tags")
@Slf4j
public class TagAdminViewController extends ProfileCommonViewController {

    public static final String TAG_FORM = "admin/tag/form";
    public static final String MESSAGE = "message";
    public static final String REDIRECT_ADMIN_TAGS = "redirect:/admin/tags";
    public static final String TAG_DTO = "tagDto";
    private final TagService tagService;
    private final BlogConfiguration blogConfiguration;

    public TagAdminViewController(PrivateMessageService privateMessageService,
                                  NotificationService notificationService,
                                  UserService userService,
                                  TagService tagService,
                                  BlogConfiguration blogConfiguration) {
        super(privateMessageService, notificationService, userService);
        this.tagService = tagService;
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

        Page<Tag> tagPage = tagService.getTags(search, pageable);

        addProfileAttributes(model, user);
        addPaginationAttributes(model, page, field, direction, search);
        paging(model, tagPage, size);

        model.addAttribute("tagPage", tagPage);

        return "admin/tag/index";
    }

    @GetMapping("{id}/edit")
    public String editView(Model model,
                           @PathVariable UUID id,
                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        Tag tag = tagService.getTagById(id);
        TagUpdateDto tagDto = new TagUpdateDto();
        tagDto.setName(tag.getName());
        model.addAttribute(TAG_DTO, tagDto);
        model.addAttribute("id", id);

        addProfileAttributes(model, user);

        return TAG_FORM;
    }

    @PostMapping("{id}/edit")
    public String editTag(@PathVariable("id") UUID id,
                          @Valid @ModelAttribute(TAG_DTO) TagUpdateDto tagDto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes ra,
                          @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        addProfileAttributes(model, user);
        if (result.hasErrors()) {
            log.warn("Validation errors while editing tag with ID {}: {}", id, result.getAllErrors());
            model.addAttribute(TAG_DTO, tagDto);
            model.addAttribute("id", id);
            model.addAttribute(MESSAGE, Message.error("Error during tag edition!"));
            return TAG_FORM;
        }

        if (tagService.isTagExists(tagDto.getName())) {
            log.warn("Attempt to edit tag with duplicate name '{}'", tagDto.getName());
            result.rejectValue("name", "error.tagDto", "Tag with this name already exists.");
            model.addAttribute("id", id);
            model.addAttribute(TAG_DTO, tagDto);
            model.addAttribute(MESSAGE, Message.error("Error during tag edition!"));

            return TAG_FORM;
        }

        try {
            tagService.updateTag(id, tagDto);
            ra.addFlashAttribute(MESSAGE, Message.info("Tag edited successfully."));
            log.info("Tag with ID {} was successfully edited by user {}", id, user.getUsername());

        } catch (Exception e) {
            log.error("Error on tag.edit", e);
            model.addAttribute(TAG_DTO, tagDto);
            model.addAttribute("id", id);
            model.addAttribute(MESSAGE, Message.error("Unknown error during tag edition!"));
            return TAG_FORM;
        }

        return REDIRECT_ADMIN_TAGS;
    }

    @GetMapping("{id}/delete")
    public String deleteView(@PathVariable UUID id, RedirectAttributes ra) {
        try {
            tagService.deleteTag(id);
            ra.addFlashAttribute(MESSAGE, Message.info("Tag deleted successfully."));
        } catch (Exception e) {
            log.error("Error on tag.delete", e);
            ra.addFlashAttribute(MESSAGE, Message.error("Unknown error during tag deletion!"));
            return REDIRECT_ADMIN_TAGS;
        }

        return REDIRECT_ADMIN_TAGS;
    }
}

