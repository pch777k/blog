package com.pch777.blog.article.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.model.ArticleStats;
import com.pch777.blog.article.dto.ArticleDto;
import com.pch777.blog.article.dto.ArticleSummaryDto;
import com.pch777.blog.article.service.ArticleMapper;
import com.pch777.blog.article.service.ArticleService;
import com.pch777.blog.article.service.ArticleStatsService;
import com.pch777.blog.category.domain.model.Category;
import com.pch777.blog.tag.dto.TagDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class ArticleApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private ArticleStatsService articleStatsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArticleMapper articleMapper;

    private PageImpl<ArticleSummaryDto> page;
    private Article article;

    Article givenArticle(String title, String content, String imageUrl) {
        return Article.builder()
                .id(UUID.randomUUID())
                .title(title)
                .titleUrl(title)
                .content(content)
                .imageUrl(imageUrl)
                .created(LocalDateTime.now())
                .category(givenCategory())
                .build();
    }

    ArticleDto givenArticleDto(String title, String content, String imageUrl, UUID categoryId, List<TagDto> tagDtoList) {
        return ArticleDto.builder()
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .categoryId(categoryId)
                .tagDtoList(tagDtoList)
                .build();
    }

    Category givenCategory() {
        return new Category("category1");
    }

    ArticleStats givenArticleStats() {
        ArticleStats articleStats = new ArticleStats();
        articleStats.setLikes(10);
        articleStats.setViews(20);
        return articleStats;
    }

    @BeforeEach
    void setUp() {
        article = givenArticle("title1", "content1", "imageUrl1");
        ArticleStats articleStats = givenArticleStats();
        articleStats.setArticle(article);

        when(articleService.getArticleByTitleUrl(article.getTitleUrl())).thenReturn(article);
        when(articleStatsService.getArticleStatsByArticleId(any())).thenReturn(articleStats);

        page = new PageImpl<>(
                List.of(articleMapper.mapToArticleSummaryDto(article))
        );

        when(articleService.getSummaryArticles(any())).thenReturn(page);
    }

    @Test
    void shouldGetArticles() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("title1"))
                .andExpect(jsonPath("$.content[0].shortContent").value("content1"))
                .andExpect(jsonPath("$.content[0].categoryName").value("category1"))
                .andExpect(jsonPath("$.content[0].likes").value(10))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.empty").value(false))
                .andExpect(
                        content().json(objectMapper.writeValueAsString(page))
                );
    }

    @Test
    void shouldGetArticle() throws Exception {
        mockMvc.perform(get("http://localhost:8080/api/v1/articles/{titleUrl}", article.getTitleUrl()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title1"))
                .andExpect(jsonPath("$.content").value("content1"))
                .andExpect(jsonPath("$.imageUrl").value("imageUrl1"))
                .andExpect(
                        content().json(objectMapper.writeValueAsString(article))
                );
    }

//    @Test
//    void shouldCreateArticle() throws Exception {
//        // given
//        ArticleDto articleDto = givenArticleDto("title", "content", "imageUrl", UUID.randomUUID(), List.of(new TagDto()));
//
//        // when
//        when(articleService.createArticle(any())).thenReturn(article);
//
//        mockMvc.perform(post("http://localhost:8080/api/v1/articles")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(articleDto))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.title").value("title1"))
//                .andExpect(jsonPath("$.content").value("content1"))
//                .andExpect(jsonPath("$.imageUrl").value("imageUrl1"))
//                .andExpect(
//                        content().json(objectMapper.writeValueAsString(article))
//                );
//    }

    @Test
    void shouldUpdateArticle() throws Exception {
        // given
        ArticleDto articleDto = givenArticleDto("title", "content", "imageUrl", UUID.randomUUID(), List.of(new TagDto()));

        // when
        when(articleService.updateArticle(any(), any(), any())).thenReturn(article);

        mockMvc.perform(put("http://localhost:8080/api/v1/articles/{id}", article.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.title").value("title1"))
                .andExpect(jsonPath("$.content").value("content1"))
                .andExpect(jsonPath("$.imageUrl").value("imageUrl1"))
                .andExpect(
                        content().json(objectMapper.writeValueAsString(article))
                );
    }

    @Test
    void shouldDeleteArticle() throws Exception {
        mockMvc.perform(delete("http://localhost:8080/api/v1/articles/{id}", article.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldLikeArticle() throws Exception {
        mockMvc.perform(post("http://localhost:8080/api/v1/articles/{id}/like", article.getId()))
                .andExpect(status().isOk());
    }
}

//    @Test
//    void shouldGetArticle() throws Exception {
//        mockMvc.perform(get("http://localhost:8080/api/v1/articles/{articleId}", article.getId()))
//                .andExpect(status().isOk())
//                .andExpect(
//                        content().json(objectMapper.writeValueAsString(article))
//                );
//    }
//
//    @Test
//    void shouldCreateArticle() throws Exception {
//        mockMvc.perform(post("http://localhost:8080/api/v1/articles")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(article))
//                )
//                .andExpect(status().isCreated())
//                .andExpect(
//                        content().json(objectMapper.writeValueAsString(article))
//                );
//    }
//
//    @Test
//    void shouldUpdateArticle() throws Exception {
//        mockMvc.perform(put("http://localhost:8080/api/v1/articles/{articleId}", article.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(article))
//                )
//                .andExpect(status().isAccepted())
//                .andExpect(
//                        content().json(objectMapper.writeValueAsString(article))
//                );
//    }
//
//    @Test
//    void shouldDeleteArticle() throws Exception {
//        mockMvc.perform(delete("http://localhost:8080/api/v1/articles/{articleId}", article.getId()))
//                .andExpect(status().isNoContent());
//    }

/*
@Test
void shouldGetEmptyListOfArticles() throws Exception {

    when(articleService.getAllArticles()).thenReturn(List.of());

    mockMvc
            .perform(get("/api/articles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(0));
}

@Test
void shouldGetAllArticles() throws Exception {
    // given
    Article firstArticle = givenArticle(1L, "first title of article", "first summary of article", "first content of article");
    Article secondArticle = givenArticle(2L, "second title of article", "second summary of article", "second content of article");

    // when
    when(articleService.getAllArticles()).thenReturn(List.of(firstArticle, secondArticle));

    // then
    mockMvc
            .perform(get("/api/articles"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$.[0].id").value(1L))
            .andExpect(jsonPath("$.[0].title").value("first title of article"))
            .andExpect(jsonPath("$.[0].summary").value("first summary of article"))
            .andExpect(jsonPath("$.[0].content").value("first content of article"))
            .andExpect(jsonPath("$.[1].id").value(2L))
            .andExpect(jsonPath("$.[1].content").value("second content of article"));
}

@Test
void shouldGetArticleById() throws Exception {
    // given
    Article firstArticle = givenArticle(1L, "first title of article", "first summary of article", "first content of article");

    // when
    when(articleService.getArticleById(anyLong())).thenReturn(Optional.of(firstArticle));

    // then
    mockMvc
            .perform(get("/api/articles/{articleId}",1))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.title").value("first title of article"))
            .andExpect(jsonPath("$.summary").value("first summary of article"))
            .andExpect(jsonPath("$.content").value("first content of article"));
}

@Test
void shouldNotFoundArticleById() throws Exception {

    when(articleService.getArticleById(anyLong())).thenReturn(Optional.empty());

    mockMvc
            .perform(get("/api/articles/{articleId}",1))
            .andDo(print())
            .andExpect(status().isNotFound());
}

@Test
void shouldGetArticlesByBlogId() throws Exception {
    // given
    Article firstArticle = givenArticle(1L, "first title of article", "first summary of article", "first content of article");
    Article secondArticle = givenArticle(2L, "second title of article", "second summary of article", "second content of article");

    // when
    when(articleService.getArticlesByBlogId(1L)).thenReturn(List.of(firstArticle, secondArticle));

    // then
    mockMvc
            .perform(get("/api/{blogId}/articles",1L))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(2))
            .andExpect(jsonPath("$.[0].id").value(1L))
            .andExpect(jsonPath("$.[0].title").value("first title of article"))
            .andExpect(jsonPath("$.[0].summary").value("first summary of article"))
            .andExpect(jsonPath("$.[0].content").value("first content of article"))
            .andExpect(jsonPath("$.[1].id").value(2L))
            .andExpect(jsonPath("$.[1].content").value("second content of article"));
}

@Test
@WithMockUser
void shouldAddArticle() throws Exception {
    // given
    ArticleDto articleDto = givenArticleDto("title", "summary", "content", Set.of("tag1", "tag2"));
    Article article = givenArticle(1L, articleDto.getTitle(), articleDto.getSummary(), articleDto.getContent());

    // when
    when(articleService.createArticle(anyLong(),any(), any())).thenReturn(article);

    // then
    mockMvc
            .perform(post("/api/{blogId}/articles", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(articleDto))
                    .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("title"))
            .andExpect(jsonPath("$.summary").value("summary"))
            .andExpect(jsonPath("$.content").value("content"));

}

@Test
void shouldNotAddArticleIfUserUnauthorized() throws Exception {
    // given
    ArticleDto articleDto = givenArticleDto("title", "summary", "content", Set.of("tag1", "tag2"));

    // when
    // then
    mockMvc.perform(post("/api/{blogId}/articles", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(articleDto))
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());

}

@WithMockUser
@Test
void shouldChangeArticle() throws Exception {
    // given
    ArticleDto articleDto = givenArticleDto("updated title", "updated summary", "updated content", Set.of("tag1", "tag2"));
    Article article = givenArticle(1L, articleDto.getTitle(), articleDto.getSummary(), articleDto.getContent());

    // when
    when(articleService.changeArticle(anyLong(), any(), any())).thenReturn(article);

    // then
    mockMvc.perform(put("/api/articles/{articleId}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(articleDto))
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("updated title"))
            .andExpect(jsonPath("$.summary").value("updated summary"))
            .andExpect(jsonPath("$.content").value("updated content"));
}

@WithMockUser
@Test
void shouldDeleteArticle() throws Exception {
    mockMvc.perform(delete("/api/articles/{articleId}", 1L))
            .andExpect(status().isOk());

    verify(articleService, times(1)).deleteArticleById(1L);
}

@WithMockUser(username = "username")
@Test
void shouldAddPhotoToBlog() throws Exception {
    // given
    Article article = givenArticle(1L, "title", "summary", "content");
    article.setUser(givenUserEntity("username"));
    MockMultipartFile photo = new MockMultipartFile("file", "photo.jpg", "image/jpg", "test data".getBytes());

    // when
    when(articleService.getArticleById(1L)).thenReturn(Optional.of(article));
    when(userSecurity.isOwnerOrAdmin(article.getUser().getUsername(), "username")).thenReturn(true);

    // then
    mockMvc.perform(multipart("/api/articles/{articleId}/images", 1L)
                    .file(photo)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isAccepted());
}

@WithMockUser(username = "username")
@Test
void shouldReturnNotFoundWhenAddingPhotoToNonExistingArticle() throws Exception {
    // given
    MockMultipartFile photo = new MockMultipartFile("file", "photo.jpg", "image/jpg", "test data".getBytes());

    // when
    when(articleService.getArticleById(1L)).thenReturn(Optional.empty());

    // then
    mockMvc.perform(multipart("/api/articles/{articleId}/images", 1L)
                    .file(photo)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
}
@WithMockUser(username = "username")
@Test
void shouldReturnForbiddenWhenUserIsNotOwnerOrAdmin() throws Exception {
    // given
    Article article = givenArticle(1L, "title", "summary", "content");
    article.setUser(givenUserEntity("guest"));
    MockMultipartFile photo = new MockMultipartFile("file", "photo.jpg", "image/png", "test data".getBytes());

    // when
    when(articleService.getArticleById(1L)).thenReturn(Optional.of(article));
    when(userSecurity.isOwnerOrAdmin(article.getUser().getUsername(), "username")).thenReturn(false);

    // then
    mockMvc.perform(multipart("/api/articles/{articleId}/images", 1L)
                    .file(photo)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
}

private Article givenArticle(Long id, String title, String summary, String content) {
    Article article = new Article();
    article.setId(id);
    article.setTitle(title);
    article.setSummary(summary);
    article.setContent(content);
    return article;
}

private ArticleDto givenArticleDto(String title, String summary, String content, Set<String> tagsDto) {
    ArticleDto articleDto = new ArticleDto();
    articleDto.setTitle(title);
    articleDto.setSummary(summary);
    articleDto.setContent(content);
    articleDto.setCategoryName("category");
    articleDto.setTagsDto(tagsDto);

    return articleDto;
}

private UserEntity givenUserEntity(String username) {
    UserEntity userEntity = new UserEntity();
    userEntity.setUsername(username);
    return userEntity;
}
*/