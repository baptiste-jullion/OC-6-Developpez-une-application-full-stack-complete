package com.openclassrooms.mddapi.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @Schema(
            example = "username123"
    )
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Schema(
            example = "user@domain.ext",
            format = "email"
    )
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Password should contain at least 8 chars, one uppercase, one lowercase, one digit and one special char",
            example = "S7rongP@ssw0rd!"
    )
    @NotBlank
    @Size(min = 8, max = 256)
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password should contain at least 8 chars, one uppercase, one lowercase, one digit and one special char"
    )
    private String password;
}
