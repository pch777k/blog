package com.pch777.blog.identity.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.identity.reader.domain.model.Reader;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.dto.EmailDto;
import com.pch777.blog.identity.user.dto.UserRegisterDto;
import com.pch777.blog.identity.user.dto.UserResponseDto;
import com.pch777.blog.identity.user.service.PasswordResetTokenService;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.identity.user.service.VerificationTokenService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthApiControllerTest {


//    @Mock
//    private VerificationTokenService verificationTokenService;
//
//    @Mock
//    private PasswordResetTokenService passwordResetTokenService;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private BlogConfiguration blogConfiguration;
//
//    @InjectMocks
//    private AuthApiController authApiController;
//
//    private MockMvc mockMvc;
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        user = new Reader();
//        user.setEmail("test@example.com");
//
//        emailDto = new EmailDto();
//        emailDto.setEmail("test@example.com");
//    }
//
//    @Test
//    void testRegisterUser() throws Exception {
//        UserRegisterDto userRegisterDto = new UserRegisterDto();
//        userRegisterDto.setUsername("test");
//        userRegisterDto.setEmail("test@example.com");
//        userRegisterDto.setPassword("password");
//        userRegisterDto.setFirstName("Joe");
//        userRegisterDto.setLastName("Doe");
//
//        User user = new Reader();
//        user.setEmail("test@example.com");
//
//
//        when(userService.registerUser(any(UserRegisterDto.class), any(String.class))).thenReturn(user);
//        UserResponseDto userResponseDto = new UserResponseDto(user);
//        mockMvc.perform(post("/api/v1/auth/signup")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userRegisterDto)))
//                .andExpect(status().isCreated());
//    }


//    @Test
//    void testSendPasswordResetEmailSuccess() throws Exception {
//        when(userService.getUserByEmail(emailDto.getEmail())).thenReturn(user);
//
//        ResponseEntity<String> response = authApiController.sendPasswordResetEmail(emailDto);
//
//        verify(passwordResetTokenService).deleteTokenByUser(user);
//        verify(passwordResetTokenService).sendPasswordResetToken(user);
//        assertEquals(ResponseEntity.status(HttpStatus.OK)
//                .body("Password reset email sent to " + emailDto.getEmail() + " successfully."), response);
//    }
//
//    @Test
//    void testSendPasswordResetEmailFailure() throws Exception {
//        when(userService.getUserByEmail(emailDto.getEmail())).thenReturn(user);
//        doThrow(new Exception("Email service failed")).when(passwordResetTokenService).sendPasswordResetToken(user);
//
//        ResponseEntity<String> response = authApiController.sendPasswordResetEmail(emailDto);
//
//        verify(passwordResetTokenService).deleteTokenByUser(user);
//        verify(passwordResetTokenService).sendPasswordResetToken(user);
//        assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("Failed to send password reset email to " + emailDto.getEmail() + "."), response);
//    }
//
//    @Test
//    void testSendPasswordResetEmailUserNotFound() {
//        when(userService.getUserByEmail(emailDto.getEmail())).thenThrow(new EntityNotFoundException("User not found"));
//
//        assertThrows(EntityNotFoundException.class, () -> authApiController.sendPasswordResetEmail(emailDto));
//    }
}