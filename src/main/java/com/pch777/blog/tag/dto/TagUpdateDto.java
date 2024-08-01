package com.pch777.blog.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TagUpdateDto {

    @NotBlank(message = "must not be blank")
    @Pattern(regexp = "^$|^.{3,30}$", message = "Length must be between 3 and 30 characters")
    private String name;
}
