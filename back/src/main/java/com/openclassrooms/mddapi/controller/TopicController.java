package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.topic.response.TopicResponse;
import com.openclassrooms.mddapi.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "Topic")
public class TopicController {
    private final TopicService topicService;

    @Operation(summary = "List all topics")
    @GetMapping("/topics")
    public ResponseEntity<List<TopicResponse>> list() {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(topicService.getAllTopics());
    }
}
