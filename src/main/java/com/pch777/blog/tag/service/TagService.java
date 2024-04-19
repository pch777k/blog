package com.pch777.blog.tag.service;

import com.pch777.blog.tag.domain.model.Tag;
import com.pch777.blog.tag.domain.repository.TagRepository;
import com.pch777.blog.tag.dto.TagDto;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TagService {

    public static final String TAG_NOT_FOUND_WITH_ID = "Tag not found with id: ";
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Tag> get10TagsByPopularity() {
        return tagRepository.find10TagsByPopularity();
    }

    @Transactional(readOnly = true)
    public Tag getTagById(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TAG_NOT_FOUND_WITH_ID + id));
    }

    @Transactional(readOnly = true)
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with name: " + name));
    }

    @Transactional
    public Tag createTag(TagDto tagDto) {
        if(isTagExists(tagDto.getName())) {
            throw new EntityExistsException("Tag already exists with name " + tagDto.getName());
        }
        Tag tag = new Tag();
        tag.setName(tagDto.getName());
        return tagRepository.save(tag);
    }

    @Transactional
    public Tag updateTag(UUID id, TagDto tagDto) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TAG_NOT_FOUND_WITH_ID + id));
        tag.setName(tagDto.getName());
        return tagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(UUID id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TAG_NOT_FOUND_WITH_ID + id));
        tag.removeArticles();
        tagRepository.delete(tag);
    }

    public boolean isTagExists(String name) {
        return tagRepository.existsByName(name);
    }
}
