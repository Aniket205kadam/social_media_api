package dev.aniket.Instagram_api.security.userClaims;

import dev.aniket.Instagram_api.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Builder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@Builder
public class JwtProvider {
    public JwtClaims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(new JwtService().secretKey.getBytes());

        Claims claims = Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String username = String.valueOf(claims.get("username"));
        String email = String.valueOf(claims.get("email"));

        return JwtClaims
                .builder()
                .username(username)
                .email(email)
                .build();
    }
}
