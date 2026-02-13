package com.openclassrooms.mddapi.security;

import com.openclassrooms.mddapi.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtUtils.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext()
                                                         .getAuthentication() == null) {
                userRepository.findByUsername(username)
                              .ifPresent(user -> {
                                  UserDetails userDetails = User.withUsername(user.getUsername())
                                                                .password(user.getPassword())
                                                                .authorities(Collections.emptyList())
                                                                .build();

                                  if (jwtUtils.isTokenValid(token, userDetails.getUsername())) {
                                      UsernamePasswordAuthenticationToken authentication =
                                              new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                      SecurityContextHolder.getContext()
                                                           .setAuthentication(authentication);
                                  }
                              });
            }
        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}

