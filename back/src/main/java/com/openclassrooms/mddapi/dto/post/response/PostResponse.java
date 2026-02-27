package com.openclassrooms.mddapi.dto.post.response;

import com.openclassrooms.mddapi.dto.comment.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
    private UUID id;
    private String title;
    private String content;
    private String author;
    private String topic;
    private List<CommentResponse> comments;
}
