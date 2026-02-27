package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.post.request.PostRequest;
import com.openclassrooms.mddapi.dto.post.response.PostResponse;
import com.openclassrooms.mddapi.entity.Post;
import com.openclassrooms.mddapi.entity.Topic;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.mapper.PostMapper;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("Should return mapped feed for subscribed topics")
    void shouldReturnFeed_forSubscribedTopics() {
        Topic tech = Topic.builder()
                          .title("Tech")
                          .build();
        User user = User.builder()
                        .username("alice")
                        .build();
        user.getSubscriptions().add(tech);

        List<Post> posts = List.of(Post.builder()
                                       .title("Post 1")
                                       .build());
        List<PostResponse> responses = List.of(PostResponse.builder()
                                                          .title("Post 1")
                                                          .build());

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(postRepository.findAllByTopicInOrderByCreatedAtDesc(user.getSubscriptions())).thenReturn(posts);
        when(postMapper.toResponseList(posts)).thenReturn(responses);

        List<PostResponse> result = postService.getFeed("alice");

        assertEquals(responses, result);
        verify(userRepository).findByUsername("alice");
        verify(postRepository).findAllByTopicInOrderByCreatedAtDesc(user.getSubscriptions());
        verify(postMapper).toResponseList(posts);
    }

    @Test
    @DisplayName("Should return all posts")
    void shouldReturnAllPosts() {
        List<Post> posts = List.of(Post.builder().build());
        List<PostResponse> responses = List.of(PostResponse.builder().title("post").build());

        when(postRepository.findAll()).thenReturn(posts);
        when(postMapper.toResponseList(posts)).thenReturn(responses);

        List<PostResponse> result = postService.getAllPosts();

        assertEquals(responses, result);
        verify(postRepository).findAll();
        verify(postMapper).toResponseList(posts);
    }

    @Test
    @DisplayName("Should create a post when topic exists")
    void shouldCreatePost_whenTopicExists() {
        UUID topicId = UUID.randomUUID();
        PostRequest request = PostRequest.builder()
                                         .title("Title")
                                         .content("Content")
                                         .topicId(topicId)
                                         .build();
        User user = User.builder()
                        .username("alice")
                        .build();
        Topic topic = Topic.builder()
                           .id(topicId)
                           .title("Tech")
                           .build();
        Post postEntity = Post.builder()
                              .title("Title")
                              .content("Content")
                              .build();
        Post savedPost = Post.builder()
                              .id(UUID.randomUUID())
                              .title("Title")
                              .content("Content")
                              .author(user)
                              .topic(topic)
                              .build();
        PostResponse response = PostResponse.builder()
                                            .id(savedPost.getId())
                                            .title("Title")
                                            .author("alice")
                                            .topic("Tech")
                                            .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));
        when(postMapper.toEntity(request)).thenReturn(postEntity);
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);
        when(postMapper.toResponse(savedPost)).thenReturn(response);

        PostResponse result = postService.createPost(request, "alice");

        assertEquals(response, result);
        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());
        assertEquals(user, captor.getValue().getAuthor());
        assertEquals(topic, captor.getValue().getTopic());
        verify(postMapper).toEntity(request);
        verify(postMapper).toResponse(savedPost);
    }

    @Test
    @DisplayName("Should throw 404 when topic is missing")
    void shouldThrow404_whenTopicMissingOnCreate() {
        PostRequest request = PostRequest.builder()
                                         .title("Title")
                                         .content("Content")
                                         .topicId(UUID.randomUUID())
                                         .build();
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(User.builder().build()));
        when(topicRepository.findById(request.getTopicId())).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> postService.createPost(request, "alice"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw 404 when post not found by id")
    void shouldThrow404_whenPostMissingById() {
        UUID postId = UUID.randomUUID();
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> postService.getPostById(postId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should return mapped post when found by id")
    void shouldReturnMappedPost_whenFoundById() {
        UUID postId = UUID.randomUUID();
        Post post = Post.builder()
                        .id(postId)
                        .title("Title")
                        .build();
        PostResponse response = PostResponse.builder()
                                            .id(postId)
                                            .title("Title")
                                            .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toResponse(post)).thenReturn(response);

        PostResponse result = postService.getPostById(postId);

        assertEquals(response, result);
        verify(postRepository).findById(postId);
        verify(postMapper).toResponse(post);
    }
}
