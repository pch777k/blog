package com.pch777.blog.identity.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.exception.authentication.TokenExpiredException;
import com.pch777.blog.exception.authentication.UserInactiveException;
import com.pch777.blog.exception.validation.UserAlreadyVerifiedException;
import com.pch777.blog.identity.reader.domain.model.Reader;
import com.pch777.blog.identity.user.domain.model.Role;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.domain.repository.UserRepository;
import com.pch777.blog.identity.user.dto.EmailDto;
import com.pch777.blog.identity.user.dto.PasswordRecoveryDto;
import com.pch777.blog.identity.user.dto.RegistrationResult;
import com.pch777.blog.identity.user.dto.UserRegisterDto;
import com.pch777.blog.identity.user.service.PasswordResetTokenService;
import com.pch777.blog.identity.user.service.UserService;
import com.pch777.blog.identity.user.service.VerificationTokenService;
import com.pch777.blog.security.SecurityConfiguration;
import com.pch777.blog.security.UserDetailsServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.pch777.blog.identity.user.dto.RegistrationResult.REGISTRATION_INVALID_LINK;
import static com.pch777.blog.identity.user.dto.RegistrationResult.REGISTRATION_SUCCESS;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthApiController.class)
@Import(SecurityConfiguration.class)
class AuthApiControllerTest {

    @MockBean
    private PasswordResetTokenService passwordResetTokenService;

    @MockBean
    private UserService userService;

    @MockBean
    private BlogConfiguration blogConfiguration;

    @MockBean
    private VerificationTokenService verificationTokenService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    private AuthApiController authApiController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setFirstName("Richard");
        userRegisterDto.setLastName("Roe");
        userRegisterDto.setUsername("richard");
        userRegisterDto.setEmail("richard@mail.com");
        userRegisterDto.setPassword("pass123");
        userRegisterDto.setConfirmPassword("pass123");

        User user = new Reader();
        user.setId(UUID.randomUUID());
        user.setUsername(userRegisterDto.getUsername());
        user.setEmail(userRegisterDto.getEmail());
        user.setRole(Role.READER);

        when(userService.registerUser(any(UserRegisterDto.class), any(String.class))).thenReturn(user);
        when(blogConfiguration.getVerificationApiBaseUrl()).thenReturn("url");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.role").value(user.getRole().name()));
    }

    @Test
    void shouldReturnValidationErrorWhenUserRegistrationFails() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setFirstName("");
        userRegisterDto.setLastName("");
        userRegisterDto.setUsername("");
        userRegisterDto.setEmail("");
        userRegisterDto.setPassword("");
        userRegisterDto.setConfirmPassword("");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasItem("firstName - must not be blank")))
                .andExpect(jsonPath("$.errors", hasItem("lastName - must not be blank")))
                .andExpect(jsonPath("$.errors", hasItem("username - must not be blank")))
                .andExpect(jsonPath("$.errors", hasItem("email - must not be blank")))
                .andExpect(jsonPath("$.errors", hasItem("password - must not be blank")))
                .andExpect(jsonPath("$.errors", hasItem("firstName - size must be between 2 and 50")))
                .andExpect(jsonPath("$.errors", hasItem("lastName - size must be between 2 and 50")))
                .andExpect(jsonPath("$.errors", hasItem("username - size must be between 3 and 50")))
                .andExpect(jsonPath("$.errors", hasItem("username - cannot contain spaces")))
                .andExpect(jsonPath("$.errors", hasItem("password - size must be between 3 and 50")));
    }

    @Test
    void shouldReturnValidationErrorWhenPasswordsMismatch() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setFirstName("Richard");
        userRegisterDto.setLastName("Roe");
        userRegisterDto.setUsername("richard");
        userRegisterDto.setEmail("richard@mail.com");
        userRegisterDto.setPassword("pass123");
        userRegisterDto.setConfirmPassword("password123");

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasItem("confirmPassword - passwords do not match")));
    }

    @Test
    void shouldVerifyEmailSuccessfully() throws Exception {
        UUID token = UUID.randomUUID();
        RegistrationResult result = REGISTRATION_SUCCESS;

        when(verificationTokenService.verifyToken(token)).thenReturn(result);

        mockMvc.perform(get("/api/v1/auth/email/verify")
                        .param("token", token.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(result.getMessage()));

        verify(verificationTokenService).verifyToken(token);
    }

    @Test
    void shouldReturnBadRequestWhenVerificationLinkIsInvalid() throws Exception {
        UUID token = UUID.randomUUID();
        RegistrationResult result = REGISTRATION_INVALID_LINK;

        when(verificationTokenService.verifyToken(token)).thenReturn(result);

        mockMvc.perform(get("/api/v1/auth/email/verify")
                        .param("token", token.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(result.getMessage()));

        verify(verificationTokenService).verifyToken(token);
    }

    @Test
    void shouldReturnBadRequestWhenVerificationLinkIsExpired() throws Exception {
        UUID token = UUID.randomUUID();
        RegistrationResult result = RegistrationResult.REGISTRATION_EXPIRED_LINK;

        when(verificationTokenService.verifyToken(token)).thenReturn(result);

        mockMvc.perform(get("/api/v1/auth/email/verify")
                        .param("token", token.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(result.getMessage()));

        verify(verificationTokenService).verifyToken(token);
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsAlreadyVerified() throws Exception {
        UUID token = UUID.randomUUID();
        RegistrationResult result = RegistrationResult.REGISTRATION_EMAIL_ALREADY_VERIFIED;

        when(verificationTokenService.verifyToken(token)).thenReturn(result);

        mockMvc.perform(get("/api/v1/auth/email/verify")
                        .param("token", token.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(result.getMessage()));

        verify(verificationTokenService).verifyToken(token);
    }

    @Test
    void shouldResendTokenSuccessfully() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail("user@example.com");
        User user = new Reader();
        user.setEmail(emailDto.getEmail());

        when(userService.getUserByEmail(emailDto.getEmail())).thenReturn(user);
        doNothing().when(verificationTokenService).resendVerificationToken(user);

        mockMvc.perform(post("/api/v1/auth/resend-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Verification email sent to user@example.com successfully."));

        verify(userService).getUserByEmail(emailDto.getEmail());
        verify(verificationTokenService).resendVerificationToken(user);
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsAlreadyVerifiedForResendToken() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail("verified@mail.com");

        when(userService.getUserByEmail(emailDto.getEmail()))
                .thenThrow(new UserAlreadyVerifiedException("Email already verified. Account is active"));

        mockMvc.perform(post("/api/v1/auth/resend-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already verified. Account is active"));

        verify(userService).getUserByEmail(emailDto.getEmail());
    }

    @Test
    void shouldReturnBadRequestWhenAccountIsInactiveForResendToken() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail("resend@mail.com");

        when(userService.getUserByEmail(emailDto.getEmail()))
                .thenThrow(new UserInactiveException("Account is inactive, please contact support"));

        mockMvc.perform(post("/api/v1/auth/resend-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Account is inactive, please contact support"));

        verify(userService).getUserByEmail(emailDto.getEmail());
    }

    @Test
    void shouldSendPasswordResetEmailSuccessfully() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail("reset@mail.com");
        User user = new Reader();
        user.setEmail(emailDto.getEmail());

        when(userService.getUserByEmail(emailDto.getEmail())).thenReturn(user);
        doNothing().when(passwordResetTokenService).deleteTokenByUser(user);
        doNothing().when(passwordResetTokenService).sendPasswordResetToken(user);

        mockMvc.perform(post("/api/v1/auth/password/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset email sent to reset@mail.com successfully."));

        verify(userService).getUserByEmail(emailDto.getEmail());
        verify(passwordResetTokenService).deleteTokenByUser(user);
        verify(passwordResetTokenService).sendPasswordResetToken(user);
    }

    @Test
    void shouldReturnInternalServerErrorWhenSendPasswordResetEmailFails() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail("reset@mail.com");
        User user = new Reader();
        user.setEmail(emailDto.getEmail());

        when(userService.getUserByEmail(emailDto.getEmail())).thenReturn(user);
        doNothing().when(passwordResetTokenService).deleteTokenByUser(user);
        doThrow(new RuntimeException()).when(passwordResetTokenService).sendPasswordResetToken(user);

        mockMvc.perform(post("/api/v1/auth/password/send-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to send password reset email to reset@mail.com."));

        verify(userService).getUserByEmail(emailDto.getEmail());
        verify(passwordResetTokenService).deleteTokenByUser(user);
        verify(passwordResetTokenService).sendPasswordResetToken(user);
    }

    @Test
    void shouldResetPasswordSuccessfully() throws Exception {
        UUID token = UUID.randomUUID();
        PasswordRecoveryDto passwordRecoveryDto = new PasswordRecoveryDto();
        passwordRecoveryDto.setNewPassword("newPassword");
        passwordRecoveryDto.setConfirmPassword("newPassword");

        doNothing().when(passwordResetTokenService).resetPassword(eq(token), any(PasswordRecoveryDto.class));

        mockMvc.perform(post("/api/v1/auth/password/reset")
                        .param("token", token.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRecoveryDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Your password has been successfully reset. You can now log in with your new password."));

        verify(passwordResetTokenService).resetPassword(eq(token), any(PasswordRecoveryDto.class));
    }

    @Test
    void shouldReturnBadRequestWhenTokenExpired() throws Exception {
        UUID token = UUID.randomUUID();
        PasswordRecoveryDto passwordRecoveryDto = new PasswordRecoveryDto();
        passwordRecoveryDto.setNewPassword("newPassword");
        passwordRecoveryDto.setConfirmPassword("newPassword");

        doThrow(new TokenExpiredException()).when(passwordResetTokenService).resetPassword(eq(token), any(PasswordRecoveryDto.class));

        mockMvc.perform(post("/api/v1/auth/password/reset")
                        .param("token", token.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRecoveryDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Oops! Password recovery link has expired. Please click the button below to send a new password recovery email."));

        verify(passwordResetTokenService).resetPassword(eq(token), any(PasswordRecoveryDto.class));
    }

    @Test
    void shouldReturnBadRequestWhenTokenInvalid() throws Exception {
        UUID token = UUID.randomUUID();
        PasswordRecoveryDto passwordRecoveryDto = new PasswordRecoveryDto();
        passwordRecoveryDto.setNewPassword("newPassword");
        passwordRecoveryDto.setConfirmPassword("newPassword");

        doThrow(new EntityNotFoundException()).when(passwordResetTokenService).resetPassword(eq(token), any(PasswordRecoveryDto.class));

        mockMvc.perform(post("/api/v1/auth/password/reset")
                        .param("token", token.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRecoveryDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Oops! The password recovery link you clicked is invalid. Please check your email for the correct link."));

        verify(passwordResetTokenService).resetPassword(eq(token), any(PasswordRecoveryDto.class));
    }

    @Test
    void shouldReturnInternalServerErrorOnException() throws Exception {
        UUID token = UUID.randomUUID();
        PasswordRecoveryDto passwordRecoveryDto = new PasswordRecoveryDto();
        passwordRecoveryDto.setNewPassword("newPassword");
        passwordRecoveryDto.setConfirmPassword("newPassword");

        doThrow(new RuntimeException()).when(passwordResetTokenService).resetPassword(eq(token), any(PasswordRecoveryDto.class));

        mockMvc.perform(post("/api/v1/auth/password/reset")
                        .param("token", token.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordRecoveryDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An error occurred while resetting the password."));

        verify(passwordResetTokenService).resetPassword(eq(token), any(PasswordRecoveryDto.class));
    }

}