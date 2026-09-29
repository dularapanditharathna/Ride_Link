package com.ridelink.account.security;

import com.ridelink.account.entity.Account;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.time.*;
import java.util.*;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final SecretKey key;
  private final long expirationMinutes;

  public JwtService(
      @Value("${ridelink.jwt.secret}") String secret,
      @Value("${ridelink.jwt.expiration-minutes:120}") long expirationMinutes) {
    this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    this.expirationMinutes = expirationMinutes;
  }

  public String generate(Account a) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(a.getId().toString())
        .claim("accountId", a.getId())
        .claim("email", a.getEmail())
        .claim("role", a.getRole().name())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(Duration.ofMinutes(expirationMinutes))))
        .signWith(key)
        .compact();
  }

  public AuthenticatedUser parse(String token) {
    Claims c = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    return new AuthenticatedUser(
        ((Number) c.get("accountId")).longValue(),
        c.get("email", String.class),
        c.get("role", String.class));
  }
}
