package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.auth.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.auth.response.AuthResponse;
import com.openclassrooms.mddapi.dto.user.response.UserResponse;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Should retrieve current user profile")
    void shouldRetrieveCurrentUser() {
        User user = User.builder()
                        .id(UUID.randomUUID())
                        .username("john")
                        .email("john@example.com")
                        .build();
        UserResponse response = UserResponse.builder()
                                            .id(user.getId())
                                            .username("john")
                                            .email("john@example.com")
                                            .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.retrieveMe("john");

        assertEquals(response, result);
        verify(userRepository).findByUsername("john");
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Should throw 404 when user not found")
    void shouldThrow404_whenUserNotFoundOnRetrieve() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.retrieveMe("ghost"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should update user and return new token")
    void shouldUpdateUser_andReturnToken() {
        User existing = User.builder()
                            .id(UUID.randomUUID())
                            .username("old")
                            .email("old@example.com")
                            .password("hash")
                            .build();
        RegisterRequest request = RegisterRequest.builder()
                                                 .username("new")
                                                 .email("new@example.com")
                                                 .password("password")
                                                 .build();
        User updated = User.builder()
                            .id(existing.getId())
                            .username("new")
                            .email("new@example.com")
                            .password("encoded")
                            .build();

        when(userRepository.findByUsername("old")).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("new")).thenReturn(false);
        doAnswer(invocation -> {
            RegisterRequest req = invocation.getArgument(0);
            User user = invocation.getArgument(1);
            user.setUsername(req.getUsername());
            user.setEmail(req.getEmail());
            return null;
        }).when(userMapper).updateEntityFromRequest(eq(request), any(User.class));
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(updated);
        when(jwtUtils.generateToken("new")).thenReturn("jwt-token");

        AuthResponse result = userService.updateMe("old", request);

        assertEquals("jwt-token", result.getToken());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("new", saved.getUsername());
        assertEquals("new@example.com", saved.getEmail());
        assertEquals("encoded", saved.getPassword());
    }

    @Test
    @DisplayName("Should throw 404 when updating missing user")
    void shouldThrow404_whenUpdatingMissingUser() {
        RegisterRequest request = RegisterRequest.builder()
                                                 .username("new")
                                                 .email("new@example.com")
                                                 .password("password")
                                                 .build();
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.updateMe("ghost", request));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw conflict when updating to an existing email")
    void shouldThrowConflict_whenEmailExists() {
        User existing = User.builder()
                            .username("user")
                            .email("user@example.com")
                            .build();
        RegisterRequest request = RegisterRequest.builder()
                                                 .username("user")
                                                 .email("taken@example.com")
                                                 .password("password")
                                                 .build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.updateMe("user", request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw conflict when updating to an existing username")
    void shouldThrowConflict_whenUsernameExists() {
        User existing = User.builder()
                            .username("user")
                            .email("user@example.com")
                            .build();
        RegisterRequest request = RegisterRequest.builder()
                                                 .username("taken")
                                                 .email("user@example.com")
                                                 .password("password")
                                                 .build();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.updateMe("user", request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }
}
