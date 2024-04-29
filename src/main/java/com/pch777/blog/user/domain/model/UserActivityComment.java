package com.pch777.blog.user.domain.model;

import com.pch777.blog.comment.domain.model.Comment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class UserActivityComment extends UserActivity {

    @ManyToOne
    private Comment comment;

    public UserActivityComment(User user, Operation operation, Comment comment) {
        this.user = user;
        this.operation = operation;
        this.comment = comment;
    }

}


