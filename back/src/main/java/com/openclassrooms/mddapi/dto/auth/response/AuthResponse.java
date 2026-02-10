package com.openclassrooms.mddapi.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    @Schema(
            description = "JWT token",
            format = "RFC 7519"
    )
    private String token;
}
