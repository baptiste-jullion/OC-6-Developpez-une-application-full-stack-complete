package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.entity.Topic;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TopicController")
@Transactional
public class TopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        topicRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
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

    @Test
    @WithMockUser(username = "john")
    @DisplayName("Should subscribe the authenticated user to a topic")
    public void shouldSubscribe_whenUserIsNotSubscribed() throws Exception {
        Topic topic = topicRepository.save(Topic.builder()
                                               .title("Tech")
                                               .description("Tech news")
                                               .build());

        User user = userRepository.save(User.builder()
                                           .username("john")
                                           .email("john@example.com")
                                           .password("password")
                                           .build());

        mockMvc.perform(post("/api/topics/" + topic.getId() + "/subscribe")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());

        User updatedUser = userRepository.findById(user.getId())
                         .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(1, updatedUser.getSubscriptions()
                                        .size());
        org.junit.jupiter.api.Assertions.assertEquals(topic.getId(), updatedUser.getSubscriptions()
                                             .get(0)
                                             .getId());
    }

    @Test
    @WithMockUser(username = "john")
    @DisplayName("Should not duplicate subscription when already subscribed")
    public void shouldNotDuplicateSubscription_whenAlreadySubscribed() throws Exception {
        Topic topic = topicRepository.save(Topic.builder()
                                               .title("Science")
                                               .description("Science updates")
                                               .build());

        User user = User.builder()
                        .username("john")
                        .email("john@example.com")
                        .password("password")
                        .build();
        user.getSubscriptions()
            .add(topic);
        userRepository.save(user);

        mockMvc.perform(post("/api/topics/" + topic.getId() + "/subscribe")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());

        User updatedUser = userRepository.findByUsername("john")
                                         .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(1, updatedUser.getSubscriptions()
                                                                    .size());
        org.junit.jupiter.api.Assertions.assertEquals(topic.getId(), updatedUser.getSubscriptions()
                                                                                 .get(0)
                                                                                 .getId());
    }

    @Test
    @WithMockUser(username = "john")
    @DisplayName("Should unsubscribe the authenticated user from a topic")
    public void shouldUnsubscribe_whenUserIsSubscribed() throws Exception {
        Topic topic = topicRepository.save(Topic.builder()
                                               .title("Health")
                                               .description("Health news")
                                               .build());

        User user = User.builder()
                        .username("john")
                        .email("john@example.com")
                        .password("password")
                        .build();
        user.getSubscriptions()
            .add(topic);
        userRepository.save(user);

        mockMvc.perform(delete("/api/topics/" + topic.getId() + "/unsubscribe")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNoContent());

        User updatedUser = userRepository.findByUsername("john")
                                         .orElseThrow();

        org.junit.jupiter.api.Assertions.assertTrue(updatedUser.getSubscriptions()
                                                               .isEmpty());
    }

    @Test
    @WithMockUser(username = "john")
    @DisplayName("Should return 404 when subscribing to a missing topic")
    public void shouldReturn404_whenSubscribingToMissingTopic() throws Exception {
        userRepository.save(User.builder()
                               .username("john")
                               .email("john@example.com")
                               .password("password")
                               .build());

        mockMvc.perform(post("/api/topics/" + UUID.randomUUID() + "/subscribe")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "ghost")
    @DisplayName("Should return 404 when the authenticated user does not exist")
    public void shouldReturn404_whenUserIsMissing() throws Exception {
        Topic topic = topicRepository.save(Topic.builder()
                                               .title("Finance")
                                               .description("Finance news")
                                               .build());

        mockMvc.perform(post("/api/topics/" + topic.getId() + "/subscribe")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }
}
