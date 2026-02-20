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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final PostMapper postMapper;

    public List<PostResponse> getAllPosts() {
        return postMapper.toResponseList(postRepository.findAll());
    }

    public PostResponse createPost(PostRequest postRequest, String username) {
        User user = userRepository.findByUsername(username)
                                  .orElseThrow();
        Topic topic = topicRepository.findById(postRequest.getTopicId())
                                     .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
        Post post = postMapper.toEntity(postRequest);

        post.setTopic(topic);
        post.setAuthor(user);
        Post savedPost = postRepository.save(post);

        return postMapper.toResponse(savedPost);
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(UUID postId) {
        Post post = postRepository.findById(postId)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        return postMapper.toResponse(post);
    }
}
