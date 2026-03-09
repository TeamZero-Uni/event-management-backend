package com.event.ems.utils;

import com.event.ems.model.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtHelp {
    private SecretKey secretKey;

    public JwtHelp(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserModel user){
        return buildToken(user, 60 * 60 * 1000 * 24);
    }
    public String generateReferenceToken(UserModel user){
        return buildToken(user, 60 * 60 * 1000 * 24 * 7);
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

    public String extractUsername(String token){
        Claims data = parse(token);
        if(data == null) return null;
        return data.get("sub").toString();
    }

    private Claims parse(String token){
        try {
            return Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isValid(String token, UserModel user) {
        return extractUsername(token).equals(user.getUsername());
    }
}
