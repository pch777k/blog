package com.pch777.blog.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @NotBlank(message = "must not be blank")
    @Size(max = 255, message = "must be at most {max} characters long")
    private String content;
}
