package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.auth.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.auth.response.AuthResponse;
import com.openclassrooms.mddapi.dto.user.response.UserResponse;
import com.openclassrooms.mddapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Retrieve the authenticated user's profile")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> retrieveMe(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(userService.retrieveMe(principal.getName()));
    }

    @Operation(summary = "Update the authenticated user's profile")
    @PutMapping("/me")
    public ResponseEntity<AuthResponse> updateMe(@Valid @RequestBody RegisterRequest userRequest, Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
                             .body(userService.updateMe(principal.getName(), userRequest));
    }
}
