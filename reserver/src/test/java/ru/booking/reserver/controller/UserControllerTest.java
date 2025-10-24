package ru.booking.reserver.controller;

import org.springframework.http.ResponseEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.booking.reserver.model.dto.auth.JwtAuthenticationResponse;
import ru.booking.reserver.model.dto.auth.LoginRequest;
import ru.booking.reserver.model.dto.auth.RegisterRequest;
import ru.booking.reserver.model.dto.user.UserDto;
import ru.booking.reserver.service.AuthenticationService;
import ru.booking.reserver.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.booking.reserver.model.Role.ROLE_ADMIN;
import static ru.booking.reserver.model.Role.ROLE_USER;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteUser_SuccessfulDeletion() {
        // Arrange
        long userId = 1L;

        // Act
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Assert
        verify(userService, times(1)).deleteUser(userId);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testCreateUser_SuccessfulCreation() {
        // Arrange
        RegisterRequest request = new RegisterRequest("test@example.com", "password");
        UserDto expectedUser = new UserDto(1L, "test@example.com", ROLE_USER);

        when(userService.createUser(request)).thenReturn(expectedUser);

        // Act
        ResponseEntity<UserDto> response = userController.createUser(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedUser, response.getBody());
        verify(userService, times(1)).createUser(request);
    }

    @Test
    void testUpdateUser_SuccessfulUpdate() {
        // Arrange
        long userId = 1L;
        UserDto updatedUser = new UserDto(userId, "updated@example.com", ROLE_ADMIN);

        when(userService.updateUser(userId, updatedUser)).thenReturn(updatedUser);

        // Act
        ResponseEntity<UserDto> response = userController.updateUser(userId, updatedUser);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedUser, response.getBody());
        verify(userService, times(1)).updateUser(userId, updatedUser);
    }

    @Test
    void testRegister_UserRegistration() {
        // Arrange
        RegisterRequest request = new RegisterRequest("user@example.com", "password");
        JwtAuthenticationResponse responseToken = new JwtAuthenticationResponse("token");

        when(authenticationService.register(request)).thenReturn(responseToken);

        // Act
        ResponseEntity<JwtAuthenticationResponse> response = userController.register(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseToken, response.getBody());
        verify(authenticationService, times(1)).register(request);
    }

    @Test
    void testLogin_UserLogin() {
        // Arrange
        LoginRequest request = new LoginRequest("user@example.com", "password");
        JwtAuthenticationResponse responseToken = new JwtAuthenticationResponse("token");

        when(authenticationService.login(request)).thenReturn(responseToken);

        // Act
        ResponseEntity<JwtAuthenticationResponse> response = userController.login(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseToken, response.getBody());
        verify(authenticationService, times(1)).login(request);
    }

    @Test
    void testDeleteUser_ZeroId() {
        // Arrange
        long userId = 0L;

        // Act
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(userService, times(1)).deleteUser(userId);
    }
}