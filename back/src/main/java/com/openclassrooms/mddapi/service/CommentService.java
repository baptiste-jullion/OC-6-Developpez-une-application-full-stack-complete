package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.comment.request.CommentRequest;
import com.openclassrooms.mddapi.dto.comment.response.CommentResponse;
import com.openclassrooms.mddapi.entity.Comment;
import com.openclassrooms.mddapi.entity.Post;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.mapper.CommentMapper;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponse createComment(UUID postId, CommentRequest request, String username) {
        User author = userRepository.findByUsername(username)
                                    .orElseThrow();
        Post post = postRepository.findById(postId)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));
        Comment comment = commentMapper.toEntity(request);
        comment.setAuthor(author);
        comment.setPost(post);

        Comment saved = commentRepository.save(comment);
        return commentMapper.toResponse(saved);
    }
}
