package com.pch777.blog.article.dto;

import com.pch777.blog.tag.dto.TagDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class ArticleDto {

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @NotBlank
    private String content;

    @NotBlank
    private String imageUrl;

    private UUID categoryId;

    @Valid
    private List<TagDto> tagDtoList = new ArrayList<>();
}
