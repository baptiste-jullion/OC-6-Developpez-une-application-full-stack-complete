package com.openclassrooms.mddapi.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @Schema(
            example = "user@domain.ext",
            format = "email"
    )
    @Email
    @NotBlank
    private String email;

    @Schema(
            example = "S7rongP@ssw0rd!"
    )
    @NotBlank
    private String password;
}
