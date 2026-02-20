package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.comment.request.CommentRequest;
import com.openclassrooms.mddapi.dto.comment.response.CommentResponse;
import com.openclassrooms.mddapi.dto.post.request.PostRequest;
import com.openclassrooms.mddapi.dto.post.response.PostResponse;
import com.openclassrooms.mddapi.service.CommentService;
import com.openclassrooms.mddapi.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;

    @Operation(summary = "List all posts")
    @GetMapping("")
    public ResponseEntity<List<PostResponse>> list() {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(postService.getAllPosts());
    }

    @Operation(summary = "Create a new post")
    @PostMapping("")
    public ResponseEntity<PostResponse> create(
            @Valid @RequestBody PostRequest postRequest,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(postService.createPost(postRequest, principal.getName()));
    }

    @Operation(summary = "Retrieve a post by ID")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> retrieve(@PathVariable UUID postId) {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(postService.getPostById(postId));
    }

    @Operation(summary = "Add a comment to an existing post")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID postId,
            @Valid @RequestBody CommentRequest commentRequest,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(commentService.createComment(postId, commentRequest, principal.getName()));
    }
}
