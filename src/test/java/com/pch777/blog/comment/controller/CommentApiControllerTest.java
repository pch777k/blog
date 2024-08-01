package com.pch777.blog.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.comment.dto.CommentDto;
import com.pch777.blog.comment.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CommentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID articleId;
    private UUID commentId;
    private Comment comment;

    @BeforeEach
    void setUp() {
        articleId = UUID.randomUUID();
        commentId = UUID.randomUUID();
        comment = new Comment();
        comment.setId(commentId);
        comment.setContent("Test comment");
        comment.setCreated(LocalDateTime.now());

        when(commentService.getCommentsByArticleId(articleId, PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(comment)));
        when(commentService.getCommentById(commentId)).thenReturn(comment);
      //  when(commentService.createComment(articleId, new CommentDto("New comment"))).thenReturn(comment);
       // when(commentService.updateComment(commentId, new CommentDto("Updated comment"))).thenReturn(comment);
    }

    @Test
    @Disabled
    void shouldGetComments() throws Exception {
        mockMvc.perform(get("/api/v1/articles/{article-id}/comments", articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(commentId.toString()))
                .andExpect(jsonPath("$.content[0].content").value("Test comment"));
    }

    @Test
    void shouldGetComment() throws Exception {
        mockMvc.perform(get("/api/v1/articles/{article-id}/comments/{comment-id}", articleId, commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.content").value("Test comment"));
    }

    @Test
    @Disabled
    void shouldCreateComment() throws Exception {
        CommentDto commentDto = new CommentDto("New comment");

        mockMvc.perform(post("/api/v1/articles/{article-id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.content").value("Test comment"));
    }

    @Test
    @Disabled
    void shouldUpdateComment() throws Exception {
        CommentDto commentDto = new CommentDto("Updated comment");

        mockMvc.perform(put("/api/v1/articles/{article-id}/comments/{comment-id}", articleId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.content").value("Test comment"));
    }

    @Test
    @Disabled
    void shouldDeleteComment() throws Exception {
        mockMvc.perform(delete("/api/v1/articles/{article-id}/comments/{comment-id}", articleId, commentId))
                .andExpect(status().isNoContent());
    }
}
