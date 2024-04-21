package com.pch777.blog.common.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "blog")
@Data
public class BlogConfiguration {

    private String name;

    @Value("${blog.article.shortContentLength}")
    private int articleShortContentLength;

    @Value("${blog.article.readSpeedWordsPerMinute}")
    private int readSpeedWordsPerMinute;

    @Value("${blog.article.archive.numberOfLastMonths}")
    private int numberOfLastMonths;

    @Value("${blog.paging.pageSize}")
    private int pageSize;
}
