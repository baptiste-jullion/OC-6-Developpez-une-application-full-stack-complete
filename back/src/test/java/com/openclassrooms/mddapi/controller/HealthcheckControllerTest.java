package com.openclassrooms.mddapi.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("HealthcheckController")
public class HealthcheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return API up and running")
    public void shouldReturnApiUpAndRunning() throws Exception {
        mockMvc.perform(get("/api/healthcheck")
                       .contentType(MediaType.TEXT_PLAIN))
               .andExpect(status().isOk())
               .andExpect(content().string("API is up and running !"));
    }
}

