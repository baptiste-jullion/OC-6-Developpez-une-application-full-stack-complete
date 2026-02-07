package com.openclassrooms.mddapi.config;

import io.swagger.v3.oas.models.Paths;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenApiCustomizer hideScalarDocsCustomizer() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (paths != null) {
                paths.keySet().removeIf(path -> path.startsWith("/docs"));
            }
        };
    }
}