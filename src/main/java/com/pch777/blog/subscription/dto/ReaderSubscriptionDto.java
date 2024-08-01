package com.pch777.blog.subscription.dto;

import com.pch777.blog.identity.reader.domain.model.Reader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReaderSubscriptionDto {
    private Reader reader;
    private LocalDateTime created;
}
