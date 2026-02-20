package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.topic.response.TopicResponse;
import com.openclassrooms.mddapi.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Tag(name = "Topic")
public class TopicController {
    private final TopicService topicService;

    @Operation(summary = "List all topics")
    @GetMapping
    public ResponseEntity<List<TopicResponse>> list() {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(topicService.getAllTopics());
    }

    @Operation(summary = "Subscribe to a topic")
    @PostMapping("/{topicId}/subscribe")
    public ResponseEntity<Void> subscribe(@PathVariable UUID topicId, Principal principal) {
        topicService.subscribe(topicId, principal.getName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                             .build();
    }

    @Operation(summary = "Unsubscribe from a topic")
    @DeleteMapping("/{topicId}/unsubscribe")
    public ResponseEntity<Void> unsubscribe(@PathVariable UUID topicId, Principal principal) {
        topicService.unsubscribe(topicId, principal.getName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                             .build();
    }
}
