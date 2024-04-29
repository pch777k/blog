package com.pch777.blog.user.domain.model;

import lombok.Getter;

@Getter
public enum Operation {
    CREATE_ARTICLE("Create Article"),
    EDIT_ARTICLE("Edit Article"),
    DELETE_ARTICLE("Delete Article"),
    CREATE_COMMENT("Create Comment"),
    EDIT_COMMENT("Edit Comment"),
    DELETE_COMMENT("Delete Comment");

    private final String displayName;

    Operation(String displayName) {
        this.displayName = displayName;
    }

}
