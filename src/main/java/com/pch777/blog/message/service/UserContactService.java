package com.pch777.blog.message.service;

import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.domain.repository.UserRepository;
import com.pch777.blog.message.domain.model.UserContact;
import com.pch777.blog.message.domain.repository.UserContactRepository;
import com.pch777.blog.message.dto.AddContactDto;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserContactService {

    private final UserContactRepository userContactRepository;
    private final UserRepository userRepository;

    public List<User> getContacts(UUID userId) {
        return userContactRepository.findByUserId(userId).stream()
                .map(UserContact::getContact)
                .toList();
    }

    @Transactional
    public UserContact addContact(AddContactDto addContactDto) {
        UUID userId = addContactDto.getUserId();
        UUID contactId = addContactDto.getContactId();
        if (userContactRepository.existsByUserIdAndContactId(userId, contactId)) {
            throw new EntityExistsException("Contact already exists with id: " + contactId);
        }
        User user = userRepository.findById(addContactDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        User contact = userRepository.findById(addContactDto.getContactId())
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + contactId));

        UserContact newContact = new UserContact(user, contact);
        return userContactRepository.save(newContact);

    }
}
