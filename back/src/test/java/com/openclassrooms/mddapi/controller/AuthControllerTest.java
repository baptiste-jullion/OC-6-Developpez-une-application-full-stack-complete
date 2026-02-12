package com.openclassrooms.mddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.dto.auth.request.LoginRequest;
import com.openclassrooms.mddapi.dto.auth.request.RegisterRequest;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController")
public class AuthControllerTest {
    private final String validUsername = "username123";
    private final String validEmail = "user@domain.ext";
    private final String validPassword = "S7trongP@ssw0rd!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    private User createValidUser() {
        return User.builder()
                   .username(validUsername)
                   .email(validEmail)
                   .password(passwordEncoder.encode(validPassword))
                   .build();
    }

    private void createAndSaveValidUser() {
        User validUser = createValidUser();
        userRepository.save(validUser);
    }

    @Test
    @DisplayName("Should register a new user When valid request is provided")
    public void shouldRegisterANewUser_whenValidRequestIsProvided() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                                                 .username(validUsername)
                                                 .email(validEmail)
                                                 .password(validPassword)
                                                 .build();

        mockMvc.perform(post("/api/auth/register")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
               )
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Should return 409 When registering a user with an email that already exists")
    public void shouldReturn409_whenRegisteringAUserWithAnEmailThatAlreadyExists() throws Exception {
        String conflictEmail = validEmail;

        userRepository.save(User.builder()
                                .username("existingUser")
                                .email(conflictEmail)
                                .password(passwordEncoder.encode(validPassword))
                                .build());

        RegisterRequest request = RegisterRequest.builder()
                                                 .username("newUser")
                                                 .email(conflictEmail)
                                                 .password(validPassword)
                                                 .build();

        mockMvc.perform(post("/api/auth/register")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
               )
               .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return 409 When registering a user with a username that already exists")
    public void shouldReturn409_whenRegisteringAUserWithAUsernameThatAlreadyExists() throws Exception {
        String username = validUsername;

        userRepository.save(User.builder()
                                .username(username)
                                .email("existing@email.co")
                                .password(passwordEncoder.encode(validPassword))
                                .build());

        RegisterRequest request = RegisterRequest.builder()
                                                 .username(username)
                                                 .email("new@email.co")
                                                 .password(validPassword)
                                                 .build();

        mockMvc.perform(post("/api/auth/register")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
               )
               .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Should return a jwt When logging in with valid credentials")
    public void shouldReturnAJwt_whenLoggingInWithValidCredentials() throws Exception {
        createAndSaveValidUser();

        LoginRequest request = LoginRequest.builder()
                                           .email(validEmail)
                                           .password(validPassword)
                                           .build();

        mockMvc.perform(post("/api/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
               )
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Should return 401 When logging in with invalid password")
    public void shouldReturn401_whenLoggingInWithInvalidPassword() throws Exception {
        createAndSaveValidUser();

        LoginRequest request = LoginRequest.builder()
                                           .email(validEmail)
                                           .password("WrongPassword123!")
                                           .build();

        mockMvc.perform(post("/api/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
               )
               .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 401 When logging in with invalid email")
    public void shouldReturn401_whenLoggingInWithInvalidEmail() throws Exception {
        createAndSaveValidUser();

        LoginRequest request = LoginRequest.builder()
                                           .email("invalid@email.co")
                                           .password(validPassword)
                                           .build();

        mockMvc.perform(post("/api/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request))
               )
               .andExpect(status().isUnauthorized());
    }
}
