package com.openclassrooms.mddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController")
public class UserControllerTest {
    private final String validPassword = "S7trongP@ssw0rd!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

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

    private User saveUser(String username, String email) {
        return userRepository.save(User.builder()
                                       .username(username)
                                       .email(email)
                                       .password(passwordEncoder.encode(validPassword))
                                       .build());
    }

    @Test
    @WithMockUser(username = "existingUser")
    @DisplayName("Should return the authenticated user's profile")
    public void shouldReturnProfile_whenUserExists() throws Exception {
        saveUser("existingUser", "existing@domain.com");

        mockMvc.perform(get("/api/users/me")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.username").value("existingUser"))
               .andExpect(jsonPath("$.email").value("existing@domain.com"));
    }

    @Test
    @WithMockUser(username = "ghost")
    @DisplayName("Should return 404 when authenticated user is missing")
    public void shouldReturn404_whenAuthenticatedUserMissing() throws Exception {
        mockMvc.perform(get("/api/users/me")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "olduser")
    @DisplayName("Should update profile and return a new token")
    public void shouldUpdateProfile_whenRequestIsValid() throws Exception {
        saveUser("olduser", "old@domain.com");

        RegisterRequest request = RegisterRequest.builder()
                                                 .username("newuser")
                                                 .email("new@domain.com")
                                                 .password(validPassword)
                                                 .build();

        mockMvc.perform(put("/api/users/me")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").isNotEmpty());

        User updated = userRepository.findByUsername("newuser")
                                     .orElseThrow();
        assertEquals("new@domain.com", updated.getEmail());
        assertTrue(passwordEncoder.matches(validPassword, updated.getPassword()));
    }

    @Test
    @WithMockUser(username = "owner")
    @DisplayName("Should return 409 when updating email to an existing one")
    public void shouldReturn409_whenUpdatingEmailToExistingOne() throws Exception {
        saveUser("owner", "owner@domain.com");
        saveUser("other", "other@domain.com");

        RegisterRequest request = RegisterRequest.builder()
                                                 .username("owner")
                                                 .email("other@domain.com")
                                                 .password(validPassword)
                                                 .build();

        mockMvc.perform(put("/api/users/me")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "owner")
    @DisplayName("Should return 409 when updating username to an existing one")
    public void shouldReturn409_whenUpdatingUsernameToExistingOne() throws Exception {
        saveUser("owner", "owner@domain.com");
        saveUser("other", "other@domain.com");

        RegisterRequest request = RegisterRequest.builder()
                                                 .username("other")
                                                 .email("owner@domain.com")
                                                 .password(validPassword)
                                                 .build();

        mockMvc.perform(put("/api/users/me")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isConflict());
    }
}
