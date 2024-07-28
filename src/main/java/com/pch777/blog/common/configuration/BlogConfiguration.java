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

    @Value("${blog.article.sortField}")
    private String articleSortField;

    @Value("${blog.article.archive.numberOfLastMonths}")
    private int numberOfLastMonths;

    @Value("${blog.paging.pageSize}")
    private int pageSize;

    @Value("${blog.article.paging.pageSize}")
    private int articlesPageSize;

    @Value("${blog.comment.paging.pageSize}")
    private int commentsPageSize;

    @Value("${blog.admin.category.paging.pageSize}")
    private int adminCategoriesPageSize;

    @Value("${blog.user.defaultAvatarUrl}")
    private String defaultAvatarUrl;

    @Value("${blog.verification.token.verificationTime}")
    private long tokenVerificationTime;

    @Value("${blog.verification.view.baseUrl}")
    private String verificationViewBaseUrl;

    @Value("${blog.verification.api.baseUrl}")
    private String verificationApiBaseUrl;

    @Value("${blog.password.reset.verificationTime}")
    private long passwordVerificationTime;

    @Value("${blog.password.reset.baseUrl}")
    private String passwordBaseUrl;

    @Value("${blog.mail.subject.verification.email}")
    private String subjectVerificationEmail;

    @Value("${blog.mail.subject.reset.password}")
    private String subjectResetPassword;

    @Value("${blog.mail.from.address}")
    private String fromAddress;

    @Value("${blog.mail.from.name}")
    private String fromName;
}
