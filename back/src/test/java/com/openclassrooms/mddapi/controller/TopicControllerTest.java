package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.entity.Topic;
import com.openclassrooms.mddapi.repository.TopicRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TopicController")
public class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TopicRepository topicRepository;

    @BeforeEach
    public void setUp() {
        topicRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        topicRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return 403 when listing topics without authentication")
    public void shouldReturn403_whenListingTopicsWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/topics")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return empty list when no topics exist")
    public void shouldReturnEmptyList_whenNoTopicsExist() throws Exception {
        mockMvc.perform(get("/api/topics")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return all topics when authenticated")
    public void shouldReturnAllTopics_whenAuthenticated() throws Exception {
        Topic topic1 = topicRepository.save(Topic.builder()
                                                 .title("Tech")
                                                 .description("Latest tech news")
                                                 .build());

        Topic topic2 = topicRepository.save(Topic.builder()
                                                 .title("Science")
                                                 .description("Science discoveries")
                                                 .build());

        mockMvc.perform(get("/api/topics")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                       topic1.getId()
                             .toString(),
                       topic2.getId()
                             .toString()
               )))
               .andExpect(jsonPath("$[*].title", containsInAnyOrder(
                       topic1.getTitle(),
                       topic2.getTitle()
               )))
               .andExpect(jsonPath("$[*].description", containsInAnyOrder(
                       topic1.getDescription(),
                       topic2.getDescription()
               )));
    }
}
