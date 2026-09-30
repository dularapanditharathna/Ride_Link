package com.ridelink.ride.controller;

import com.ridelink.ride.dto.RideDtos.*;
import com.ridelink.ride.security.SecuritySupport.AuthenticatedUser;
import com.ridelink.ride.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {
  private final RideService s;

  public RideController(RideService s) {
    this.s = s;
  }

  @PostMapping
  @PreAuthorize("hasRole('PASSENGER')")
  @Operation(summary = "Request a ride and assign the first eligible driver")
  ResponseEntity<RideResponse> create(
      @AuthenticationPrincipal AuthenticatedUser u,
      @Valid @RequestBody CreateRideRequest r,
      @RequestHeader("Authorization") String a) {
    return ResponseEntity.status(201).body(s.create(u, r, a));
  }

  @GetMapping("/{id}")
  RideResponse get(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser u) {
    return s.get(id, u);
  }

  @GetMapping("/my")
  List<RideResponse> my(@AuthenticationPrincipal AuthenticatedUser u) {
    return s.mine(u);
  }

  @PostMapping("/{id}/accept")
  @PreAuthorize("hasRole('DRIVER')")
  RideResponse accept(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser u) {
    return s.accept(id, u);
  }

  @PostMapping("/{id}/start")
  @PreAuthorize("hasRole('DRIVER')")
  RideResponse start(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser u) {
    return s.start(id, u);
  }

  @PostMapping("/{id}/complete")
  @PreAuthorize("hasRole('DRIVER')")
  RideResponse complete(
      @PathVariable Long id,
      @AuthenticationPrincipal AuthenticatedUser u,
      @RequestHeader("Authorization") String a) {
    return s.complete(id, u, a);
  }

  @PostMapping("/{id}/cancel")
  RideResponse cancel(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser u) {
    return s.cancel(id, u);
  }
}
