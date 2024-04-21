package com.pch777.blog.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @Size(max = 255)
    @NotBlank
    private String content;
}
