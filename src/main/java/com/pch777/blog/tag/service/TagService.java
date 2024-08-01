package com.pch777.blog.tag.service;

import com.pch777.blog.exception.authentication.ForbiddenException;
import com.pch777.blog.exception.resource.TagNotFoundException;
import com.pch777.blog.identity.user.domain.model.Role;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.tag.domain.model.Tag;
import com.pch777.blog.tag.domain.repository.TagRepository;
import com.pch777.blog.tag.dto.TagDto;
import com.pch777.blog.tag.dto.TagUpdateDto;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;
    private final UserService userService;

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
                .orElseThrow(() -> new TagNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Tag getTagByName(String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new TagNotFoundException(name));
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
    public Tag updateTag(UUID id, TagDto tagDto, String username) {
        Tag tag = validateAdminRoleAndFindTag(id, username);
        tag.setName(tagDto.getName());
        return tagRepository.save(tag);
    }

    @Transactional
    public Tag updateTag(UUID id, TagUpdateDto tagDto) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new TagNotFoundException(id));
        if(isTagExists(tagDto.getName())) {
            throw new EntityExistsException("Tag already exists with name " + tagDto.getName());
        }
        tag.setName(tagDto.getName());
        return tagRepository.save(tag);
    }

    @Transactional
    @PreAuthorize("hasAuthority('TAG_DELETE')")
    public void deleteTag(UUID id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new TagNotFoundException(id));
        tag.removeArticles();
        tagRepository.delete(tag);
    }

    private Tag validateAdminRoleAndFindTag(UUID tagId, String username) {
        User user = userService.getUserByUsername(username);
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Access denied");
        }
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new TagNotFoundException(tagId));
    }

    public boolean isTagExists(String name) {
        return tagRepository.existsByName(name);
    }

    public Page<Tag> getTags(String search, Pageable pageable) {
        return tagRepository.findAllByNameContainingIgnoreCase(search, pageable);
    }
}
