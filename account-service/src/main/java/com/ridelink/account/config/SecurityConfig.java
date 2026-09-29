package com.ridelink.account.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.account.exception.ApiError;
import com.ridelink.account.security.JwtAuthenticationFilter;
import java.time.Instant;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain chain(HttpSecurity h, JwtAuthenticationFilter f, ObjectMapper om)
      throws Exception {
    return h.csrf(c -> c.disable())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            a ->
                a.requestMatchers(
                        "/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(
                        (q, r, x) -> {
                          r.setStatus(401);
                          r.setContentType(MediaType.APPLICATION_JSON_VALUE);
                          om.writeValue(
                              r.getOutputStream(),
                              new ApiError(
                                  Instant.now(),
                                  401,
                                  "Unauthorized",
                                  "Authentication is required",
                                  q.getRequestURI()));
                        })
                    .accessDeniedHandler(
                        (q, r, x) -> {
                          r.setStatus(403);
                          r.setContentType(MediaType.APPLICATION_JSON_VALUE);
                          om.writeValue(
                              r.getOutputStream(),
                              new ApiError(
                                  Instant.now(),
                                  403,
                                  "Forbidden",
                                  "You do not have permission",
                                  q.getRequestURI()));
                        }))
        .addFilterBefore(f, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
