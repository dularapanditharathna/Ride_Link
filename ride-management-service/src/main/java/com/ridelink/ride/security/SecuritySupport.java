package com.ridelink.ride.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.*;
import org.springframework.web.filter.OncePerRequestFilter;

public final class SecuritySupport {
  private SecuritySupport() {}

  public record AuthenticatedUser(Long accountId, String email, String role) {}

  @Service
  public static class JwtService {
    private final SecretKey key;

    public JwtService(@Value("${ridelink.jwt.secret}") String s) {
      key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(s));
    }

    public AuthenticatedUser parse(String t) {
      Claims c = Jwts.parser().verifyWith(key).build().parseSignedClaims(t).getPayload();
      return new AuthenticatedUser(
          ((Number) c.get("accountId")).longValue(),
          c.get("email", String.class),
          c.get("role", String.class));
    }
  }

  @Component
  public static class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwt;

    public JwtFilter(JwtService j) {
      jwt = j;
    }

    protected void doFilterInternal(HttpServletRequest q, HttpServletResponse r, FilterChain c)
        throws ServletException, IOException {
      String h = q.getHeader("Authorization");
      if (h != null && h.startsWith("Bearer "))
        try {
          var u = jwt.parse(h.substring(7));
          SecurityContextHolder.getContext()
              .setAuthentication(
                  new UsernamePasswordAuthenticationToken(
                      u, null, List.of(new SimpleGrantedAuthority("ROLE_" + u.role()))));
        } catch (Exception e) {
          SecurityContextHolder.clearContext();
        }
      c.doFilter(q, r);
    }
  }
}
