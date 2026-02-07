package com.openclassrooms.mddapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Healthcheck")
public class HealthcheckController {
    @Operation(summary = "Healthcheck")
    @GetMapping("/api/healthcheck")
    public String healthcheck() {
        return "API is up and running !";
    }
}
