package com.pch777.blog.tag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagDto {

    @NotBlank
    @Size(min = 3, max = 20)
    private String name;
}
