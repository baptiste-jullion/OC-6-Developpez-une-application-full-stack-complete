package com.openclassrooms.mddapi.dto.post.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
