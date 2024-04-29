package com.pch777.blog.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {


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
                case "ROLE_ADMIN" -> {
                    return "dashboard-admin";
                }
                case "ROLE_AUTHOR" -> {
                    return "dashboard-author";
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
