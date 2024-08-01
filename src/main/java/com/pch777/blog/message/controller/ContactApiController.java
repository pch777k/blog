package com.pch777.blog.message.controller;

import com.pch777.blog.message.service.UserContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/contacts")
public class ContactApiController {

    private final UserContactService userContactService;


//    @PostMapping
//    public ResponseEntity<UserContact> addContact(@RequestBody AddContactDto addContactDto) {
//        UserContact newContact = contactService.addContact(addContactDto);
//        return ResponseEntity.status(HttpStatus.CREATED).body(newContact);
//    }
}
