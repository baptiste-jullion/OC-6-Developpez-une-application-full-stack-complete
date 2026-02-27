package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.topic.response.TopicResponse;
import com.openclassrooms.mddapi.entity.Topic;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.mapper.TopicMapper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TopicService")
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private TopicMapper topicMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TopicService topicService;

    @Test
    @DisplayName("Should return all topics")
    void shouldReturnAllTopics() {
        List<Topic> topics = List.of(Topic.builder()
                                          .id(UUID.randomUUID())
                                          .title("Tech")
                                          .description("Tech desc")
                                          .build());
        List<TopicResponse> responses = List.of(TopicResponse.builder()
                                                             .id(topics.get(0).getId())
                                                             .title("Tech")
                                                             .description("Tech desc")
                                                             .build());

        when(topicRepository.findAll()).thenReturn(topics);
        when(topicMapper.toResponseList(topics)).thenReturn(responses);

        List<TopicResponse> result = topicService.getAllTopics();

        assertEquals(responses, result);
        verify(topicRepository).findAll();
        verify(topicMapper).toResponseList(topics);
    }

    @Test
    @DisplayName("Should subscribe a user when not subscribed")
    void shouldSubscribeUser_whenNotSubscribed() {
        UUID topicId = UUID.randomUUID();
        Topic topic = Topic.builder()
                           .id(topicId)
                           .title("Science")
                           .description("Science desc")
                           .build();
        User user = User.builder()
                        .id(UUID.randomUUID())
                        .username("john")
                        .email("john@example.com")
                        .subscriptions(new ArrayList<>())
                        .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        topicService.subscribe(topicId, "john");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(1, captor.getValue().getSubscriptions().size());
        assertEquals(topicId, captor.getValue().getSubscriptions().get(0).getId());
    }

    @Test
    @DisplayName("Should not duplicate subscription when already subscribed")
    void shouldNotDuplicateSubscription_whenAlreadySubscribed() {
        UUID topicId = UUID.randomUUID();
        Topic topic = Topic.builder()
                           .id(topicId)
                           .build();
        User user = User.builder()
                        .username("john")
                        .subscriptions(new ArrayList<>(List.of(topic)))
                        .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        topicService.subscribe(topicId, "john");

        verify(userRepository, never()).save(any(User.class));
        assertEquals(1, user.getSubscriptions().size());
    }

    @Test
    @DisplayName("Should throw 404 when subscribing missing user")
    void shouldThrow404_whenSubscribingMissingUser() {
        UUID topicId = UUID.randomUUID();
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> topicService.subscribe(topicId, "ghost"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw 404 when subscribing missing topic")
    void shouldThrow404_whenSubscribingMissingTopic() {
        UUID topicId = UUID.randomUUID();
        User user = User.builder()
                        .username("john")
                        .subscriptions(new ArrayList<>())
                        .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> topicService.subscribe(topicId, "john"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should unsubscribe user when subscribed")
    void shouldUnsubscribeUser_whenSubscribed() {
        UUID topicId = UUID.randomUUID();
        Topic topic = Topic.builder()
                           .id(topicId)
                           .build();
        User user = User.builder()
                        .username("john")
                        .subscriptions(new ArrayList<>(List.of(topic)))
                        .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        topicService.unsubscribe(topicId, "john");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(0, captor.getValue().getSubscriptions().size());
    }

    @Test
    @DisplayName("Should not save when unsubscribing a non subscribed user")
    void shouldNotSave_whenUserNotSubscribed() {
        UUID topicId = UUID.randomUUID();
        Topic topic = Topic.builder()
                           .id(topicId)
                           .build();
        User user = User.builder()
                        .username("john")
                        .subscriptions(new ArrayList<>())
                        .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        topicService.unsubscribe(topicId, "john");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw 404 when unsubscribing missing user")
    void shouldThrow404_whenUnsubscribingMissingUser() {
        UUID topicId = UUID.randomUUID();
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> topicService.unsubscribe(topicId, "ghost"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw 404 when unsubscribing missing topic")
    void shouldThrow404_whenUnsubscribingMissingTopic() {
        UUID topicId = UUID.randomUUID();
        User user = User.builder()
                        .username("john")
                        .subscriptions(new ArrayList<>())
                        .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(topicRepository.findById(topicId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> topicService.unsubscribe(topicId, "john"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
