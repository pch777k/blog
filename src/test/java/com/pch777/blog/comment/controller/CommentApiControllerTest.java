package com.pch777.blog.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.comment.dto.CommentDto;
import com.pch777.blog.comment.service.CommentService;
import com.pch777.blog.exception.resource.CommentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
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
    private CommentDto commentDto;
    private CommentDto updatedCommentDto;

    @BeforeEach
    void setUp() {
        articleId = UUID.randomUUID();
        commentId = UUID.randomUUID();
        commentDto = new CommentDto("New comment");
        updatedCommentDto = new CommentDto("Updated comment");
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setContent("New comment");
        comment.setCreated(LocalDateTime.now());

        when(commentService.getCommentsByArticleId(articleId, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(comment)));
        when(commentService.getCommentById(commentId)).thenReturn(comment);
        when(commentService.createComment(eq(articleId), any(CommentDto.class), any(String.class)))
                .thenReturn(comment);
    }

    @Test
    void shouldGetComments() throws Exception {
        mockMvc.perform(get("/api/v1/articles/{article-id}/comments", articleId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(commentId.toString()))
                .andExpect(jsonPath("$.content[0].content").value("New comment"))
                .andExpect(jsonPath("$.content[0].created").exists())
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    @WithMockUser(authorities = {"COMMENT_CREATE"})
    void shouldCreateComment() throws Exception {
        String commentDtoJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(post("/api/v1/articles/{article-id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDtoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.content").value("New comment"))
                .andExpect(header().string("Location", String.format("http://localhost/api/v1/articles/%s/comments/%s", articleId, commentId)));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"COMMENT_UPDATE"})
    void shouldReturnForbiddenWhenUserLacksCommentCreatePermission() throws Exception {
        String commentDtoJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(post("/api/v1/articles/{article-id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDtoJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void shouldReturnRedirectWhenUserUnauthorizedWantCreateComment() throws Exception {
        String commentDtoJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(post("/api/v1/articles/{article-id}/comments", articleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDtoJson))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"COMMENT_UPDATE"})
    void shouldUpdateComment() throws Exception {
        Comment updatedComment = new Comment();
        updatedComment.setId(commentId);
        updatedComment.setContent(updatedCommentDto.getContent());
        updatedComment.setCreated(LocalDateTime.now());

        when(commentService.updateComment(eq(commentId), any(CommentDto.class), any(String.class)))
                .thenReturn(updatedComment);

        String updatedCommentDtoJson = objectMapper.writeValueAsString(updatedCommentDto);

        mockMvc.perform(put("/api/v1/articles/{article-id}/comments/{comment-id}", articleId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCommentDtoJson))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(commentId.toString()))
                .andExpect(jsonPath("$.content").value("Updated comment"));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"COMMENT_UPDATE"})
    void shouldReturnNotFoundWhenCommentDoesNotExist() throws Exception {
        when(commentService.updateComment(eq(commentId), any(CommentDto.class), any(String.class)))
                .thenThrow(new CommentNotFoundException(commentId));

        String updatedCommentDtoJson = objectMapper.writeValueAsString(updatedCommentDto);

        mockMvc.perform(put("/api/v1/articles/{article-id}/comments/{comment-id}", articleId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedCommentDtoJson))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(authorities = {"COMMENT_CREATE"})
    void shouldReturnForbiddenWhenUserLacksCommentUpdatePermission() throws Exception {
        String commentDtoJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(put("/api/v1/articles/{article-id}/comments/{comment-id}", articleId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDtoJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void shouldReturnRedirectWhenUserUnauthorizedWantUpdateComment() throws Exception {
        String commentDtoJson = objectMapper.writeValueAsString(commentDto);

        mockMvc.perform(put("/api/v1/articles/{article-id}/comments/{comment-id}", articleId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentDtoJson))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"COMMENT_DELETE"})
    void shouldDeleteComment() throws Exception {
        doNothing().when(commentService).deleteComment(commentId, "user");

        mockMvc.perform(delete("/api/v1/articles/{article-id}/comments/{comment-id}", articleId, commentId))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(commentId, "user");
    }

    @Test
    @WithMockUser(username = "user", authorities = {"COMMENT_DELETE"})
    void shouldReturnNotFoundWhenCommentDoesNotExistDuringDeleting() throws Exception {
        doThrow(new CommentNotFoundException(commentId)).when(commentService).deleteComment(commentId, "user");

        mockMvc.perform(delete("/api/v1/articles/{article-id}/comments/{comment-id}", articleId, commentId))
                .andExpect(status().isNotFound());

        verify(commentService, times(1)).deleteComment(commentId, "user");
    }

    @Test
    @WithMockUser(username = "user", authorities = {"COMMENT_UPDATE"})
    void shouldReturnForbiddenWhenUserLacksCommentDeletePermission() throws Exception {
        doNothing().when(commentService).deleteComment(commentId, "user");

        mockMvc.perform(delete("/api/v1/articles/{article-id}/comments/{comment-id}", articleId, commentId))
                .andExpect(status().isForbidden());

        verify(commentService, never()).deleteComment(commentId, "user");
    }
}