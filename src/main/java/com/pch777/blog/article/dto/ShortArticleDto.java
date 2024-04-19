package com.pch777.blog.article.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ShortArticleDto {
    private String title;
    private String titleUrl;
    private String imageUrl;
    private LocalDateTime created;

}
