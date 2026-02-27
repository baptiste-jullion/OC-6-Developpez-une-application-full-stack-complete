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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("Should create a comment when post exists")
    void shouldCreateComment_whenPostExists() {
        UUID postId = UUID.randomUUID();
        CommentRequest request = CommentRequest.builder()
                                               .content("Nice")
                                               .build();
        User user = User.builder()
                        .username("alice")
                        .build();
        Post post = Post.builder()
                        .id(postId)
                        .build();
        Comment commentEntity = Comment.builder()
                                       .content("Nice")
                                       .build();
        Comment savedComment = Comment.builder()
                                      .id(UUID.randomUUID())
                                      .content("Nice")
                                      .author(user)
                                      .post(post)
                                      .build();
        CommentResponse response = CommentResponse.builder()
                                                  .id(savedComment.getId())
                                                  .content("Nice")
                                                  .author("alice")
                                                  .build();

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentMapper.toEntity(request)).thenReturn(commentEntity);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(commentMapper.toResponse(savedComment)).thenReturn(response);

        CommentResponse result = commentService.createComment(postId, request, "alice");

        assertEquals(response, result);
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        assertEquals(user, captor.getValue().getAuthor());
        assertEquals(post, captor.getValue().getPost());
    }

    @Test
    @DisplayName("Should throw 404 when post is missing")
    void shouldThrow404_whenPostMissing() {
        UUID postId = UUID.randomUUID();
        CommentRequest request = CommentRequest.builder()
                                               .content("Nice")
                                               .build();
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(User.builder().build()));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> commentService.createComment(postId, request, "alice"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    @DisplayName("Should throw when user is missing")
    void shouldThrow_whenUserMissing() {
        UUID postId = UUID.randomUUID();
        CommentRequest request = CommentRequest.builder()
                                               .content("Nice")
                                               .build();
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> commentService.createComment(postId, request, "ghost"));
    }
}
