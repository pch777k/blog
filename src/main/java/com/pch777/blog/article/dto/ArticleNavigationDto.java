package com.pch777.blog.article.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ArticleNavigationDto {
    private String prev;
    private String next;
}
