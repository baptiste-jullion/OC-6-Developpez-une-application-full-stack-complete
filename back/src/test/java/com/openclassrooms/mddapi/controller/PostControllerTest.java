package com.openclassrooms.mddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.dto.comment.request.CommentRequest;
import com.openclassrooms.mddapi.dto.post.request.PostRequest;
import com.openclassrooms.mddapi.entity.Post;
import com.openclassrooms.mddapi.entity.Topic;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("PostController")
public class PostControllerTest {
    private final String validPassword = "S7trongP@ssw0rd!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    public void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        topicRepository.deleteAll();
    }

    @AfterEach
    public void tearDown() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        topicRepository.deleteAll();
    }

    private User createUser(String username) {
        return userRepository.save(User.builder()
                                       .username(username)
                                       .email(username + "@email.com")
                                       .password(validPassword)
                                       .build());
    }

    private Topic createTopic(String title) {
        return topicRepository.save(Topic.builder()
                                         .title(title)
                                         .description(title + " description")
                                         .build());
    }

    private Post createPost(User author, Topic topic, String title, String content) {
        return postRepository.save(Post.builder()
                                       .title(title)
                                       .content(content)
                                       .author(author)
                                       .topic(topic)
                                       .build());
    }

    private Post createPost(User author, Topic topic, String title, String content, LocalDateTime createdAt) {
        return postRepository.save(Post.builder()
                                       .title(title)
                                       .content(content)
                                       .author(author)
                                       .topic(topic)
                                       .createdAt(createdAt)
                                       .build());
    }

    @Test
    @WithMockUser(username = "alice")
    @DisplayName("Should list posts when authenticated")
    public void shouldListPosts_whenAuthenticated() throws Exception {
        User author = createUser("alice");
        Topic topic = createTopic("Tech");
        createPost(author, topic, "First post", "Content");

        mockMvc.perform(get("/api/posts")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].title").value("First post"))
               .andExpect(jsonPath("$[0].content").value("Content"))
               .andExpect(jsonPath("$[0].author").value("alice"))
               .andExpect(jsonPath("$[0].topic").value("Tech"));
    }

    @Test
    @WithMockUser(username = "alice")
    @DisplayName("Should create a post when request is valid")
    public void shouldCreatePost_whenRequestIsValid() throws Exception {
        createUser("alice");
        Topic topic = createTopic("Science");

        PostRequest request = PostRequest.builder()
                                         .title("How to learn Java?")
                                         .content("Share your resources")
                                         .topicId(topic.getId())
                                         .build();

        mockMvc.perform(post("/api/posts")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").isNotEmpty())
               .andExpect(jsonPath("$.title").value("How to learn Java?"))
               .andExpect(jsonPath("$.author").value("alice"))
               .andExpect(jsonPath("$.topic").value("Science"));
    }

    @Test
    @WithMockUser(username = "alice")
    @DisplayName("Should return 404 when creating a post for an unknown topic")
    public void shouldReturn404_whenCreatingPostForUnknownTopic() throws Exception {
        createUser("alice");

        PostRequest request = PostRequest.builder()
                                         .title("No topic")
                                         .content("Missing topic")
                                         .topicId(UUID.randomUUID())
                                         .build();

        mockMvc.perform(post("/api/posts")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "alice")
    @DisplayName("Should retrieve a post by id")
    public void shouldRetrievePostById_whenItExists() throws Exception {
        User author = createUser("alice");
        Topic topic = createTopic("Math");
        Post post = createPost(author, topic, "Algebra", "Algebra content");

        mockMvc.perform(get("/api/posts/" + post.getId())
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(post.getId()
                                                     .toString()))
               .andExpect(jsonPath("$.title").value("Algebra"))
               .andExpect(jsonPath("$.author").value("alice"))
               .andExpect(jsonPath("$.topic").value("Math"));
    }

    @Test
    @WithMockUser(username = "alice")
    @DisplayName("Should return 404 when retrieving an unknown post")
    public void shouldReturn404_whenRetrievingUnknownPost() throws Exception {
        mockMvc.perform(get("/api/posts/" + UUID.randomUUID())
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "alice")
    @DisplayName("Should add a comment to an existing post")
    public void shouldAddComment_whenPostExists() throws Exception {
        User author = createUser("alice");
        Topic topic = createTopic("History");
        Post post = createPost(author, topic, "Ancient Rome", "History content");

        CommentRequest request = CommentRequest.builder()
                                               .content("Great post!")
                                               .build();

        mockMvc.perform(post("/api/posts/" + post.getId() + "/comments")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").isNotEmpty())
               .andExpect(jsonPath("$.content").value("Great post!"))
               .andExpect(jsonPath("$.author").value("alice"))
               .andExpect(jsonPath("$.createdAt").exists());

        org.junit.jupiter.api.Assertions.assertEquals(1, commentRepository.count());
    }

    @Test
    @WithMockUser(username = "alice")
    @DisplayName("Should return 404 when adding a comment to a missing post")
    public void shouldReturn404_whenAddingCommentToMissingPost() throws Exception {
        createUser("alice");

        CommentRequest request = CommentRequest.builder()
                                               .content("I have thoughts")
                                               .build();

        mockMvc.perform(post("/api/posts/" + UUID.randomUUID() + "/comments")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 403 when requesting feed without authentication")
    public void shouldReturn403_whenRequestingFeedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/posts/feed")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice")
    @DisplayName("Should return feed for subscribed topics in descending order")
    public void shouldReturnFeed_whenUserSubscribed() throws Exception {
        User alice = createUser("alice");
        User bob = createUser("bob");

        Topic tech = createTopic("Tech");
        Topic science = createTopic("Science");
        Topic history = createTopic("History");

        alice.getSubscriptions()
             .add(tech);
        alice.getSubscriptions()
             .add(science);
        userRepository.save(alice);

        Post older = createPost(bob, science, "Science News", "Older content", LocalDateTime.now()
                                                                                            .minusDays(1));
        Post latest = createPost(bob, tech, "Latest Tech", "New content", LocalDateTime.now());
        Post unrelated = createPost(bob, history, "History Post", "Should be excluded", LocalDateTime.now()
                                                                                                     .minusDays(2));

        mockMvc.perform(get("/api/posts/feed")
                       .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].id").value(latest.getId()
                                                          .toString()))
               .andExpect(jsonPath("$[1].id").value(older.getId()
                                                         .toString()))
               .andExpect(jsonPath("$[*].id", not(hasItem(unrelated.getId()
                                                                   .toString()))));
    }
}
