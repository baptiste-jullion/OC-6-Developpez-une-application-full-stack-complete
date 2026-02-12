package com.openclassrooms.mddapi.config;

import io.swagger.v3.oas.models.Paths;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    private final String scalarPath;

    public OpenAPIConfig(@Value("${scalar.path:/docs}") String scalarPath) {
        this.scalarPath = scalarPath;
    }

    @Bean
    public OpenApiCustomizer hideScalarDocsCustomizer() {
        return openApi -> {
            Paths paths = openApi.getPaths();
            if (paths != null) {
                paths.keySet()
                     .removeIf(path -> path.startsWith(scalarPath));
            }
        };
    }
}