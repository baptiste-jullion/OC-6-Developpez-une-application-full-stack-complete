package com.openclassrooms.mddapi.security;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {
    private final SecretKey key = Jwts.SIG.HS256.key()
                                                .build();

    public String generateToken(String username) {
        return Jwts.builder()
                   .subject(username)
                   .issuedAt(new Date())
                   .expiration(new Date(System.currentTimeMillis() + 86400000))
                   .signWith(key)
                   .compact();
    }
}