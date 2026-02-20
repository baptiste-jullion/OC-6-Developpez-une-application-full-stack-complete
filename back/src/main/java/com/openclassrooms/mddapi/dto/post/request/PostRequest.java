package com.openclassrooms.mddapi.dto.post.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequest {
    @Schema(example = "How to learn Java?")
    @NotBlank
    private String title;

    @Schema(example = "I am new to programming and I want to learn Java. Can you give me some tips?")
    @NotBlank
    private String content;

    @Schema(description = "ID of the topic this post belongs to", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    @NotNull
    private UUID topicId;
}
