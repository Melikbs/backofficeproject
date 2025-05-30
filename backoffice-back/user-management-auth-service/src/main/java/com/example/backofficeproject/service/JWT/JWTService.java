package com.example.backofficeproject.service.JWT;

import com.example.backofficeproject.model.Roles;
import com.example.backofficeproject.model.Users;
import com.example.backofficeproject.repositories.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;

import java.util.*;
import java.util.function.Function;

@Service
public class JWTService {

    private final UserRepo userRepo;
    private final String secretKey;

    @Autowired
    public JWTService(UserRepo userRepo) {
        this.userRepo = userRepo;
        this.secretKey = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
    }

    public String generateToken(String username) {
        Users user = userRepo.findByEmail(username);


        if (user == null) {
            throw new RuntimeException("User not found");
        }


        String role = "ROLE_"+user.getRole().iterator().next().getLabel();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role",user.getRole().stream()
                .findFirst()
                .map(Roles::getLabel)
                .orElse("ROLE_User"));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)

                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600 * 60 * 1000)) // Expiration 1 hour
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
