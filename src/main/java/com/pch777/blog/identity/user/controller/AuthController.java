package com.pch777.blog.identity.user.controller;

import com.pch777.blog.common.dto.Message;
import com.pch777.blog.identity.user.domain.model.Role;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.dto.UserCreateByAdminDto;
import com.pch777.blog.identity.user.dto.UserRegisterDto;
import com.pch777.blog.identity.user.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RequiredArgsConstructor
@Controller
public class AuthController {

    public static final String REGISTER = "register";
    public static final String MESSAGE = "message";
    public static final String ADMIN_FORM = "admin/form";
    private final UserService userService;


    @GetMapping("/create-user")
    public String registerView(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        model.addAttribute("userCreateByAdminDto", new UserCreateByAdminDto());
        model.addAttribute("user", user);
        return ADMIN_FORM;
    }

    @PostMapping("/create-user")
    public String createUser(@Valid @ModelAttribute("userCreateByAdminDto") UserCreateByAdminDto userCreateByAdminDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByUsername(userDetails.getUsername());
        User createdUser;
        if (bindingResult.hasErrors()) {
            model.addAttribute(MESSAGE, Message.error("Error during user registration!"));
            model.addAttribute("user", user);
            log.error("Error on user.create");
            return ADMIN_FORM;
        }
        try {
            createdUser = userService.createUser(userCreateByAdminDto);
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("User created successfully!"));
            log.info("User created successfully!");

        } catch (EntityExistsException | EntityNotFoundException e) {
            log.error("Error on user.create: " + e.getMessage());
            model.addAttribute(MESSAGE, Message.error(e.getMessage()));
            model.addAttribute("user", user);
            return ADMIN_FORM;
        } catch (Exception e) {
            log.error("Error on user.create", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during user creation!"));
            model.addAttribute("user", user);
            return ADMIN_FORM;
        }

        return redirectUrlByRole(createdUser.getRole()) + createdUser.getId();
    }

    private String redirectUrlByRole(Role role) {
        if (role == Role.READER) {
            return "redirect:/admin/reader/";
        } else {
            return "redirect:/admin/author/";
        }
    }


    @GetMapping("/signup")
    public String signupView(Model model) {
        model.addAttribute("userRegisterDto", new UserRegisterDto());
        return REGISTER;
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("userRegisterDto") UserRegisterDto userRegisterDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(MESSAGE, Message.error("Error during user registration!"));
            log.error("Error on user.signup");
            return REGISTER;
        }
        try {
            userService.registerUser(userRegisterDto);
            redirectAttributes.addFlashAttribute(MESSAGE, Message.info("User registered successfully!"));
            log.info("User registered successfully!");

        } catch (EntityExistsException | EntityNotFoundException e) {
            log.error("Error on user.signup: " + e.getMessage());
            model.addAttribute(MESSAGE, Message.error(e.getMessage()));
            return REGISTER;
        } catch (Exception e) {
            log.error("Error on user.signup", e);
            model.addAttribute(MESSAGE, Message.error("Unknown error during user registration!"));
            return REGISTER;
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String role = authentication.getAuthorities().iterator().next().getAuthority();
            switch (role) {
                case "ROLE_AUTHOR" -> {
                    return "redirect:/users/profile";
                }
                case "ROLE_ADMIN" -> {
                    return "redirect:/users/dashboard";
                }
                case "ROLE_READER" -> {
                    return "redirect:/";
                }
                default -> throw new IllegalStateException("Unexpected value: " + role);
            }
        }
        return "redirect:/login";
    }


}
