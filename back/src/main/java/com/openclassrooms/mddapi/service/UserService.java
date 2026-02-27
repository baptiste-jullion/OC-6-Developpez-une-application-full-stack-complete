package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.auth.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.auth.response.AuthResponse;
import com.openclassrooms.mddapi.dto.user.response.UserResponse;
import com.openclassrooms.mddapi.entity.User;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserResponse retrieveMe(String username) {
        User user = userRepository.findByUsername(username)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return userMapper.toResponse(user);
    }

    public AuthResponse updateMe(String username, RegisterRequest userRequest) {
        User user = userRepository.findByUsername(username)
                                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (userRequest.getEmail() != null && !userRequest.getEmail()
                                                          .equals(user.getEmail())) {
            if (userRepository.existsByEmail(userRequest.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }
        }

        if (userRequest.getUsername() != null && !userRequest.getUsername()
                                                             .equals(user.getUsername())) {
            if (userRepository.existsByUsername(userRequest.getUsername())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use");
            }
        }

        userMapper.updateEntityFromRequest(userRequest, user);

        if (userRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        User updatedUser = userRepository.save(user);

        String token = jwtUtils.generateToken(updatedUser.getUsername());
        return new AuthResponse(token);
    }
}
