package dev.aniket.Instagram_api.security.service;

import dev.aniket.Instagram_api.exception.UserException;
import dev.aniket.Instagram_api.model.User;
import dev.aniket.Instagram_api.service.UserService;
import dev.aniket.Instagram_api.service.impl.UserServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private ApplicationContext context;
    public String secretKey;

    public JwtService() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGenerator.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    public String generateToken(String username) throws UserException {
        UserService userService = context.getBean(UserServiceImpl.class);
        User user = userService.findUserByUsername(username);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("email", user.getEmail());
        claims.put("roles", "USER");
        claims.put("accountStatus", "ACTIVE");

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(user.getId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (60 * 60 * 30000)))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

//    public String extractUsernameFromToken(String token) {
//        // extract the username from jwt token
//        return extractClaim(token, Claims::getSubject);
//    }

    public String extractUsernameFromToken(String token) {
        // extract the username from jwt token
        return extractClaim(token, claims -> claims.get("username", String.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUsernameFromToken(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
