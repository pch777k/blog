package com.pch777.blog.identity.reader.service;

import com.pch777.blog.exception.resource.UserNotFoundException;
import com.pch777.blog.identity.reader.domain.model.Reader;
import com.pch777.blog.identity.reader.domain.repository.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReaderService {

    private final ReaderRepository readerRepository;

    @Transactional(readOnly = true)
    public Reader getReaderById(UUID id) {
        return readerRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Reader", id));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<Reader> getReaders(String search, Pageable pageable) {
        return readerRepository.findAllReaders(search, pageable);
    }

}
