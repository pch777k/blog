package com.pch777.blog.article.dto;

import com.pch777.blog.tag.dto.TagDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArticleDto {

    @NotBlank(message = "must not be blank")
    @Size(message = "size must be between {min} and {max}", min = 3, max = 100)
    private String title;

    @NotBlank(message = "must not be blank")
    private String content;

    @NotBlank(message = "must not be blank")
    private String imageUrl;

    @NotNull(message = "must not be null")
    private UUID categoryId;

    @Valid
    private List<TagDto> tagDtoList = new ArrayList<>();
}
