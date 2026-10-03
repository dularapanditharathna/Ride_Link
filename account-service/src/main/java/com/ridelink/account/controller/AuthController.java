package com.ridelink.account.controller;

import com.ridelink.account.dto.request.*;
import com.ridelink.account.dto.response.*;
import com.ridelink.account.entity.Role;
import com.ridelink.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AccountService service;

  public AuthController(AccountService s) {
    service = s;
  }

  @PostMapping("/register/passenger")
  @Operation(summary = "Register a passenger")
  ResponseEntity<AccountResponse> passenger(@Valid @RequestBody RegisterRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.register(r, Role.PASSENGER));
  }

  @PostMapping("/register/driver")
  @Operation(summary = "Register a driver")
  ResponseEntity<AccountResponse> driver(@Valid @RequestBody RegisterRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(service.register(r, Role.DRIVER));
  }

  @PostMapping("/login")
  @Operation(summary = "Login and receive a JWT")
  LoginResponse login(@Valid @RequestBody LoginRequest r) {
    return service.login(r);
  }
}
