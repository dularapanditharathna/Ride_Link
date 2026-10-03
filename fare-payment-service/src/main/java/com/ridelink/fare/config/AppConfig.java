package com.ridelink.fare.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.fare.exception.ExceptionSupport.ApiError;
import com.ridelink.fare.security.SecuritySupport.JwtFilter;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import java.time.Instant;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class AppConfig {
  @Bean
  SecurityFilterChain security(HttpSecurity h, JwtFilter f, ObjectMapper o) throws Exception {
    return h.csrf(c -> c.disable())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            a ->
                a.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(
                        (q, r, x) -> {
                          r.setStatus(401);
                          r.setContentType(MediaType.APPLICATION_JSON_VALUE);
                          o.writeValue(
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
                          o.writeValue(
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

  @Bean
  OpenAPI api() {
    return new OpenAPI()
        .info(new Info().title("RideLink Fare & Payment API").version("1.0"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "bearerAuth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
  }
}
