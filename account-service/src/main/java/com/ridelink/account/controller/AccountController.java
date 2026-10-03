package com.ridelink.account.controller;

import com.ridelink.account.dto.request.*;
import com.ridelink.account.dto.response.*;
import com.ridelink.account.security.AuthenticatedUser;
import com.ridelink.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
  private final AccountService service;

  public AccountController(AccountService s) {
    service = s;
  }

  @GetMapping("/me")
  @Operation(summary = "Get the current account")
  AccountResponse me(@AuthenticationPrincipal AuthenticatedUser u) {
    return service.me(u);
  }

  @PutMapping("/me")
  @Operation(summary = "Update the current account")
  AccountResponse update(
      @AuthenticationPrincipal AuthenticatedUser u, @Valid @RequestBody UpdateAccountRequest r) {
    return service.updateMe(u, r);
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Change an account status (ADMIN only)")
  AccountResponse status(@PathVariable Long id, @Valid @RequestBody UpdateStatusRequest r) {
    return service.updateStatus(id, r);
  }
}
