package com.pch777.blog.validation;

import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class OldPasswordValidator implements ConstraintValidator<ValidOldPassword, String> {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public boolean isValid(String oldPassword, ConstraintValidatorContext context) {
        if (oldPassword == null) {
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        return passwordEncoder.matches(oldPassword, user.getPassword());
    }
}
