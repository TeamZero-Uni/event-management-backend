package com.event.ems.utils;

import com.event.ems.model.UserModel;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class Jwt {
    private SecretKey secretKey;

    public Jwt(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserModel user){
        return buildToken(user, 60 * 60 * 1000 * 24);
    }

    public String buildToken(UserModel user, long expiry){
        return Jwts.builder()
                .claim("role", user.getRole())
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(secretKey)
                .compact();
    }
}
