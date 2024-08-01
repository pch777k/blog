package com.pch777.blog.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StatisticsDto {
    private long totalArticles;
    private long totalComments;
    private long totalSubscriptions;
    private long totalAuthors;
    private long totalReaders;
    private long totalLikes;
    private long totalTags;
}
