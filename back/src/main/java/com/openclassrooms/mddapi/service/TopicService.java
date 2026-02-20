package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.topic.response.TopicResponse;
import com.openclassrooms.mddapi.entity.Topic;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicMapper topicMapper;
    private final UserRepository userRepository;

    public List<TopicResponse> getAllTopics() {
        return topicMapper.toResponseList(topicRepository.findAll());
    }

    @Transactional
    public void subscribe(UUID topicId, String username) {
        User user = userRepository.findByUsername(username)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Topic topic = topicRepository.findById(topicId)
                                     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));

        boolean alreadySubscribed = user.getSubscriptions()
                                        .stream()
                                        .anyMatch(subscription -> subscription.getId()
                                                                              .equals(topicId));

        if (!alreadySubscribed) {
            user.getSubscriptions()
                .add(topic);
            userRepository.save(user);
        }
    }

    @Transactional
    public void unsubscribe(UUID topicId, String username) {
        User user = userRepository.findByUsername(username)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        topicRepository.findById(topicId)
                       .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));

        boolean removed = user.getSubscriptions()
                              .removeIf(subscription -> subscription.getId()
                                                                    .equals(topicId));
        if (removed) {
            userRepository.save(user);
        }
    }
}
