package com.pch777.blog.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = OldPasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOldPassword {
    String message() default "invalid old password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
