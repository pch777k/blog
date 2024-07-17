package com.pch777.blog.validation;

import com.pch777.blog.identity.user.dto.ChangePasswordDto;
import com.pch777.blog.identity.user.dto.RecoverPasswordDto;
import com.pch777.blog.identity.user.dto.UserRegisterDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof UserRegisterDto userRegisterDto) {
            return validatePasswords(userRegisterDto.getPassword(), userRegisterDto.getConfirmPassword(), context);
        } else if (obj instanceof ChangePasswordDto changePasswordDto) {
            return validatePasswords(changePasswordDto.getNewPassword(), changePasswordDto.getConfirmPassword(), context);
        } else if (obj instanceof RecoverPasswordDto recoverPasswordDto) {
            return validatePasswords(recoverPasswordDto.getNewPassword(), recoverPasswordDto.getConfirmPassword(), context);
        }
        return false;
    }

    private boolean validatePasswords(String password, String confirmPassword, ConstraintValidatorContext context) {
        boolean isValid = password.equals(confirmPassword);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("confirmPassword")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
