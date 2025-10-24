package ru.booking.reserver.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.booking.reserver.model.Role;
import ru.booking.reserver.model.dto.auth.RegisterRequest;
import ru.booking.reserver.model.dto.user.UserDto;
import ru.booking.reserver.model.entity.UserEntity;
import ru.booking.reserver.model.mappers.UserMapper;
import ru.booking.reserver.repository.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock authentication for getCurrentUser()
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("testuser");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testCreateUserWithRegisterRequest_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest("testuser", "password");
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testuser");
        userEntity.setPassword("encodedPassword");
        userEntity.setRole(Role.ROLE_ADMIN);

        UserDto userDto = new UserDto(1L, "testuser", Role.ROLE_ADMIN);

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(userEntity);
        when(userMapper.toDto(any())).thenReturn(userDto);

        // Act
        UserDto result = userService.createUser(request);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.name());
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testCreateUserWithRegisterRequest_ExistingUsername() {
        // Arrange
        RegisterRequest request = new RegisterRequest("testuser", "password");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.createUser(request));
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testCreateUserEntity_Success() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testuser");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        // Act
        UserEntity result = userService.createUser(userEntity);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void testCreateUserEntity_ExistingUsername() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("testuser");

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.createUser(userEntity));
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUser_ExistingId() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> userService.deleteUser(1L));
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, never()).deleteById(1L);
    }

    @Test
    void testDeleteUser_NonExistingId() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        UserDto updateUserDto = new UserDto(1L, "newuser", Role.ROLE_USER);
        UserEntity existingUser = new UserEntity();
        existingUser.setId(1L);
        existingUser.setUsername("olduser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toDto(existingUser)).thenReturn(updateUserDto);

        // Act
        UserDto result = userService.updateUser(1L, updateUserDto);

        // Assert
        assertNotNull(result);
        assertEquals("newuser", existingUser.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByUsername("newuser");
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Arrange
        UserDto updateUserDto = new UserDto(1L, "newuser", Role.ROLE_USER);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> userService.updateUser(1L, updateUserDto));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUser_DuplicateUsername() {
        // Arrange
        UserDto updateUserDto = new UserDto(1L, "existinguser", Role.ROLE_USER);
        UserEntity existingUser = new UserEntity();
        existingUser.setId(1L);
        existingUser.setUsername("olduser");

        UserEntity otherUser = new UserEntity();
        otherUser.setId(2L);
        otherUser.setUsername("existinguser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(otherUser));

        // Act & Assert
        assertThrows(DuplicateKeyException.class, () -> userService.updateUser(1L, updateUserDto));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findByUsername("existinguser");
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetByUsername_UserExists() {
        // Arrange
        UserEntity user = new UserEntity();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserEntity result = userService.getByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetByUsername_UserNotExists() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.getByUsername("testuser"));
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testGetCurrentUser() {
        // Arrange
        UserEntity expectedUser = new UserEntity();
        expectedUser.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(expectedUser));

        // Act
        UserEntity result = userService.getCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }
}