package com.openclassrooms.mddapi.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @Schema(example = "username123 or user@domain.ext")
    @NotBlank
    private String login;

    @Schema(example = "S7rongP@ssw0rd!")
    @NotBlank
    private String password;
}
