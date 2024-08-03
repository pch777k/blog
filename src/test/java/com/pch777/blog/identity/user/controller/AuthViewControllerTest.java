package com.pch777.blog.identity.user.controller;

import com.pch777.blog.common.configuration.BlogConfiguration;
import com.pch777.blog.exception.authentication.TokenExpiredException;
import com.pch777.blog.exception.resource.UserNotFoundException;
import com.pch777.blog.identity.reader.domain.model.Reader;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.domain.model.VerificationToken;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static com.pch777.blog.identity.user.dto.PasswordRecoveryResult.*;
import static com.pch777.blog.identity.user.dto.RegistrationResult.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfiguration.class)
@WebMvcTest(AuthViewController.class)
class AuthViewControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private VerificationTokenService verificationTokenService;

    @MockBean
    private PasswordResetTokenService passwordResetTokenService;

    @MockBean
    private BlogConfiguration blogConfiguration;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    private AuthViewController authViewController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldDisplaySignupViewSuccessfully() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("userRegisterDto"));
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setFirstName("Richard");
        userRegisterDto.setLastName("Roe");
        userRegisterDto.setUsername("richard");
        userRegisterDto.setEmail("richard@mail.com");
        userRegisterDto.setPassword("pass123");
        userRegisterDto.setConfirmPassword("pass123");

        when(blogConfiguration.getVerificationViewBaseUrl()).thenReturn("http://localhost:8080/email/verify");

        mockMvc.perform(post("/signup")
                        .flashAttr("userRegisterDto", userRegisterDto))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.REGISTRATION_VERIFICATION))
                .andExpect(model().attributeExists(AuthViewController.TITLE))
                .andExpect(model().attributeExists(AuthViewController.MESSAGE));

        verify(userService).registerUser(eq(userRegisterDto), anyString());
    }

    @Test
    void shouldReturnValidationErrorWhenUserRegistrationFails() throws Exception {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setFirstName("");
        userRegisterDto.setLastName("");
        userRegisterDto.setUsername("short");
        userRegisterDto.setEmail("invalid-email");
        userRegisterDto.setPassword("pass123");
        userRegisterDto.setConfirmPassword("wrong");

        mockMvc.perform(post("/signup")
                        .flashAttr("userRegisterDto", userRegisterDto))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.REGISTER))
                .andExpect(model().attributeExists(AuthViewController.MESSAGE))
                .andExpect(model().attribute(AuthViewController.MESSAGE,
                        REGISTRATION_ERROR_VALIDATION.getMessage()));
    }

    @Test
    void shouldResendVerificationTokenSuccessfully() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail("test@mail.com");

        User user = new Reader();
        user.setEmail("test@mail.com");

        when(userService.getUserByEmail("test@mail.com")).thenReturn(user);

        mockMvc.perform(post("/resend-token")
                        .flashAttr("emailDto", emailDto))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.REGISTRATION_VERIFICATION))
                .andExpect(model().attribute(AuthViewController.TITLE,
                        REGISTRATION_VERIFICATION_LINK_RESENT.getTitle()))
                .andExpect(model().attribute(AuthViewController.MESSAGE,
                        REGISTRATION_VERIFICATION_LINK_RESENT.getMessage()));

        verify(userService).getUserByEmail("test@mail.com");
        verify(verificationTokenService).resendVerificationToken(user);
    }

    @Test
    void shouldHandleUserNotFound() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail("notfound@mail.com");

        when(userService.getUserByEmail("notfound@mail.com"))
                .thenThrow(new UserNotFoundException("User", "username"));

        mockMvc.perform(post("/resend-token")
                        .flashAttr("emailDto", emailDto))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));

        verify(userService).getUserByEmail("notfound@mail.com");
        verify(verificationTokenService, never()).resendVerificationToken(any(User.class));
    }

    @Test
    void shouldDisplayForgotPasswordView() throws Exception {
        mockMvc.perform(get("/password/forgot"))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.FORGOT_PASSWORD))
                .andExpect(model().attributeExists("emailDto"));
    }

    @Test
    void shouldSendPasswordResetEmail() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail("test@mail.com");

        User user = new Reader();
        when(userService.getUserByEmail(emailDto.getEmail())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/password/send-email")
                        .flashAttr("emailDto", emailDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(AuthViewController.PASSWORD_RECOVERY_VERIFICATION))
                .andExpect(MockMvcResultMatchers.model().attribute(AuthViewController.TITLE, LINK_SENT.getTitle()))
                .andExpect(MockMvcResultMatchers.model().attribute(AuthViewController.MESSAGE, LINK_SENT.getMessage()));
    }

    @Test
    void shouldHandleValidationErrors() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail("");

        mockMvc.perform(MockMvcRequestBuilders.post("/password/send-email")
                        .flashAttr("emailDto", emailDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(AuthViewController.FORGOT_PASSWORD))
                .andExpect(MockMvcResultMatchers.model().attribute(AuthViewController.MESSAGE, ERROR_EMAIL_VALIDATION.getMessage()));
    }

    @Test
    void shouldHandleUserNotFoundException() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setEmail("notfound@mail.com");

        when(userService.getUserByEmail(emailDto.getEmail())).thenThrow(new UserNotFoundException("User", "notfound"));

        mockMvc.perform(MockMvcRequestBuilders.post("/password/send-email")
                        .flashAttr("emailDto", emailDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(AuthViewController.FORGOT_PASSWORD))
                .andExpect(MockMvcResultMatchers.model().attribute(AuthViewController.MESSAGE, ERROR_EMAIL_NOT_FOUND.getMessage() + emailDto.getEmail()));
    }

    @Test
    void shouldDisplayResetPasswordFormWithValidToken() throws Exception {
        UUID validToken = UUID.randomUUID();

        doNothing().when(passwordResetTokenService).validateToken(validToken);

        mockMvc.perform(MockMvcRequestBuilders.get("/password/reset")
                        .param("token", validToken.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.PASSWORD_RECOVERY_FORM))
                .andExpect(model().attributeExists(AuthViewController.TOKEN))
                .andExpect(model().attributeExists("passwordRecoveryDto"));
    }

    @Test
    void shouldHandleInvalidToken() throws Exception {
        UUID invalidToken = UUID.randomUUID();

        doThrow(new EntityNotFoundException("PasswordToken not found"))
                .when(passwordResetTokenService).validateToken(invalidToken);

        mockMvc.perform(MockMvcRequestBuilders.get("/password/reset")
                        .param("token", invalidToken.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.PASSWORD_RECOVERY_VERIFICATION))
                .andExpect(model().attribute(AuthViewController.TITLE, INVALID_LINK.getTitle()))
                .andExpect(model().attribute(AuthViewController.MESSAGE, INVALID_LINK.getMessage()))
                .andExpect(model().attributeExists(AuthViewController.TITLE))
                .andExpect(model().attributeExists(AuthViewController.MESSAGE));
    }

    @Test
    void shouldHandleExpiredToken() throws Exception {
        UUID expiredToken = UUID.randomUUID();

        doThrow(new TokenExpiredException("Token has expired"))
                .when(passwordResetTokenService).validateToken(expiredToken);

        mockMvc.perform(MockMvcRequestBuilders.get("/password/reset")
                        .param("token", expiredToken.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.PASSWORD_RECOVERY_VERIFICATION))
                .andExpect(model().attribute(AuthViewController.TITLE, EXPIRED_LINK.getTitle()))
                .andExpect(model().attribute(AuthViewController.MESSAGE, EXPIRED_LINK.getMessage()))
                .andExpect(model().attributeExists(AuthViewController.TITLE))
                .andExpect(model().attributeExists(AuthViewController.MESSAGE));
    }

    @Test
    void shouldHandleUnknownErrorDuringPasswordRecovery() throws Exception {
        UUID token = UUID.randomUUID();

        doThrow(new RuntimeException("Unknown error"))
                .when(passwordResetTokenService).validateToken(token);

        mockMvc.perform(MockMvcRequestBuilders.get("/password/reset")
                        .param("token", token.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.PASSWORD_RECOVERY_VERIFICATION))
                .andExpect(model().attribute(AuthViewController.TITLE, UNKNOWN_ERROR.getTitle()))
                .andExpect(model().attribute(AuthViewController.MESSAGE, UNKNOWN_ERROR.getMessage()))
                .andExpect(model().attributeExists(AuthViewController.TITLE))
                .andExpect(model().attributeExists(AuthViewController.MESSAGE));
    }

    @Test
    void shouldRecoverPasswordSuccessfully() throws Exception {
        UUID validToken = UUID.randomUUID();
        PasswordRecoveryDto passwordRecoveryDto = new PasswordRecoveryDto();
        passwordRecoveryDto.setNewPassword("newPass123");
        passwordRecoveryDto.setConfirmPassword("newPass123");

        doNothing().when(passwordResetTokenService).resetPassword(validToken, passwordRecoveryDto);

        mockMvc.perform(post("/password/reset")
                        .param("token", validToken.toString())
                        .flashAttr("passwordRecoveryDto", passwordRecoveryDto))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.PASSWORD_RECOVERY_VERIFICATION))
                .andExpect(model().attribute(AuthViewController.TITLE, SUCCESS.getTitle()))
                .andExpect(model().attribute(AuthViewController.MESSAGE, SUCCESS.getMessage()));
    }

    @Test
    void shouldReturnPasswordRecoveryFormWhenValidationFails() throws Exception {
        UUID validToken = UUID.randomUUID();
        PasswordRecoveryDto passwordRecoveryDto = new PasswordRecoveryDto();
        passwordRecoveryDto.setNewPassword("newPass123");
        passwordRecoveryDto.setConfirmPassword("NewPass123");

        mockMvc.perform(post("/password/reset")
                        .param("token", validToken.toString())
                        .flashAttr("passwordRecoveryDto", passwordRecoveryDto))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.PASSWORD_RECOVERY_FORM))
                .andExpect(model().attribute(AuthViewController.MESSAGE, ERROR_PASSWORD_VALIDATION.getMessage()))
                .andExpect(model().attributeExists(AuthViewController.TOKEN));
    }

    @Test
    void shouldHandleInvalidTokenDuringPasswordRecovery() throws Exception {
        UUID invalidToken = UUID.randomUUID();
        PasswordRecoveryDto passwordRecoveryDto = new PasswordRecoveryDto();
        passwordRecoveryDto.setNewPassword("newPass123");
        passwordRecoveryDto.setConfirmPassword("newPass123");

        doThrow(new EntityNotFoundException("PasswordToken not found"))
                .when(passwordResetTokenService).resetPassword(invalidToken, passwordRecoveryDto);

        mockMvc.perform(post("/password/reset")
                        .param("token", invalidToken.toString())
                        .flashAttr("passwordRecoveryDto", passwordRecoveryDto))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.PASSWORD_RECOVERY_VERIFICATION))
                .andExpect(model().attribute(AuthViewController.TITLE, INVALID_LINK.getTitle()))
                .andExpect(model().attribute(AuthViewController.MESSAGE, INVALID_LINK.getMessage()));
    }


    @Test
    void shouldHandleExpiredTokenDuringPasswordRecovery() throws Exception {
        UUID expiredToken = UUID.randomUUID();
        PasswordRecoveryDto passwordRecoveryDto = new PasswordRecoveryDto();
        passwordRecoveryDto.setNewPassword("newPass123");
        passwordRecoveryDto.setConfirmPassword("newPass123");

        doThrow(new TokenExpiredException("Token has expired"))
                .when(passwordResetTokenService).resetPassword(expiredToken, passwordRecoveryDto);

        mockMvc.perform(post("/password/reset")
                        .param("token", expiredToken.toString())
                        .flashAttr("passwordRecoveryDto", passwordRecoveryDto))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.PASSWORD_RECOVERY_VERIFICATION))
                .andExpect(model().attribute(AuthViewController.TITLE, EXPIRED_LINK.getTitle()))
                .andExpect(model().attribute(AuthViewController.MESSAGE, EXPIRED_LINK.getMessage()));
    }


    @Test
    void shouldVerifyEmailSuccessfully() throws Exception {
        UUID token = UUID.randomUUID();
        RegistrationResult result = REGISTRATION_SUCCESS;

        when(verificationTokenService.verifyToken(token)).thenReturn(result);

        mockMvc.perform(get("/email/verify")
                        .param("token", token.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.REGISTRATION_VERIFICATION))
                .andExpect(model().attribute(AuthViewController.TITLE, result.getTitle()))
                .andExpect(model().attribute(AuthViewController.MESSAGE, result.getMessage()));
    }


    @Test
    void shouldHandleExpiredTokenDuringVerifyEmail() throws Exception {
        UUID token = UUID.randomUUID();
        RegistrationResult result = REGISTRATION_EXPIRED_LINK;
        VerificationToken verificationToken = new VerificationToken();
        User user = new Reader();
        user.setEmail("expired@mail.com");
        verificationToken.setUser(user);

        when(verificationTokenService.verifyToken(token)).thenReturn(result);
        when(verificationTokenService.getToken(token)).thenReturn(verificationToken);

        mockMvc.perform(get("/email/verify")
                        .param("token", token.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.REGISTRATION_VERIFICATION))
                .andExpect(model().attribute(AuthViewController.TITLE, result.getTitle()))
                .andExpect(model().attribute(AuthViewController.MESSAGE, result.getMessage()))
                .andExpect(model().attributeExists("emailDto"));
    }

    @Test
    void shouldHandleAlreadyVerifiedEmail() throws Exception {
        UUID token = UUID.randomUUID();
        RegistrationResult result = REGISTRATION_EMAIL_ALREADY_VERIFIED;

        when(verificationTokenService.verifyToken(token)).thenReturn(result);

        mockMvc.perform(get("/email/verify")
                        .param("token", token.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.REGISTRATION_VERIFICATION))
                .andExpect(model().attribute(AuthViewController.TITLE, result.getTitle()))
                .andExpect(model().attribute(AuthViewController.MESSAGE, result.getMessage()));
    }

    @Test
    void shouldHandleInvalidTokenDuringVerifyEmail() throws Exception {
        UUID token = UUID.randomUUID();
        RegistrationResult result = REGISTRATION_INVALID_LINK;

        when(verificationTokenService.verifyToken(token)).thenReturn(REGISTRATION_INVALID_LINK);

        mockMvc.perform(get("/email/verify")
                        .param("token", token.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(AuthViewController.REGISTRATION_VERIFICATION))
                .andExpect(model().attribute(AuthViewController.TITLE, result.getTitle()))
                .andExpect(model().attribute(AuthViewController.MESSAGE, result.getMessage()));
    }

    @Test
    void shouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @WithMockUser(roles = "AUTHOR")
    void shouldRedirectToAuthorProfile() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/profile"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRedirectToAdminDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @WithMockUser(roles = "READER")
    void shouldRedirectToHomePageForReaderRole() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void shouldRedirectAnonymousUserToLoginPage() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

}