package com.alifetvaci.credit.userservice.service;

import static org.junit.jupiter.api.Assertions.*;
import com.alifetvaci.credit.userservice.api.exception.GenericException;
import com.alifetvaci.credit.userservice.controller.request.LoginRequest;
import com.alifetvaci.credit.userservice.controller.request.RegisterRequest;
import com.alifetvaci.credit.userservice.controller.response.LoginResponse;
import com.alifetvaci.credit.userservice.delegate.auth.service.AuthService;
import com.alifetvaci.credit.userservice.repository.UserRepository;
import com.alifetvaci.credit.userservice.repository.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserService userService;

    @Test
    void register_successfulRegistration() {
        // Arrange
        RegisterRequest request = new RegisterRequest("1234567890", "John", "Doe", "password");
        when(userRepository.findByIdentificationNumber(request.getIdentificationNumber())).thenReturn(Optional.empty());

        // Act
        userService.register(request);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_userAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest("1234567890", "John", "Doe", "password");
        User existingUser = new User("1234567890", "John", "Doe", "encodedPassword");
        when(userRepository.findByIdentificationNumber(request.getIdentificationNumber())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(GenericException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_successfulLogin() {
        // Arrange
        LoginRequest request = new LoginRequest("1234567890", "password");
        User user = new User("1234567890", "John", "Doe", "encodedPassword");
        String token = "validToken";
        when(userRepository.findByIdentificationNumber(request.getIdentificationNumber())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(authService.createToken(user)).thenReturn(token);

        // Act
        LoginResponse response = userService.login(request);

        // Assert
        assertEquals(token, response.getToken());
    }

    @Test
    void login_userNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest("1234567890", "password");
        when(userRepository.findByIdentificationNumber(request.getIdentificationNumber())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GenericException.class, () -> userService.login(request));
    }

    @Test
    void login_incorrectPassword() {
        // Arrange
        LoginRequest request = new LoginRequest("1234567890", "password");
        User user = new User("1234567890", "John", "Doe", "encodedPassword");
        when(userRepository.findByIdentificationNumber(request.getIdentificationNumber())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(GenericException.class, () -> userService.login(request));
    }
}