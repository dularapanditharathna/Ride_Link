package com.ridelink.driver.controller;

import com.ridelink.driver.dto.DriverDtos.*;
import com.ridelink.driver.security.SecuritySupport.AuthenticatedUser;
import com.ridelink.driver.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
  private final DriverService s;

  public DriverController(DriverService s) {
    this.s = s;
  }

  @PostMapping("/profile")
  @PreAuthorize("hasRole('DRIVER')")
  @Operation(summary = "Create driver profile")
  ResponseEntity<ProfileResponse> create(
      @AuthenticationPrincipal AuthenticatedUser u, @Valid @RequestBody ProfileRequest r) {
    return ResponseEntity.status(201).body(s.create(u, r));
  }

  @GetMapping("/me")
  @PreAuthorize("hasRole('DRIVER')")
  ProfileResponse me(@AuthenticationPrincipal AuthenticatedUser u) {
    return s.me(u);
  }

  @PutMapping("/me")
  @PreAuthorize("hasRole('DRIVER')")
  ProfileResponse update(
      @AuthenticationPrincipal AuthenticatedUser u, @Valid @RequestBody ProfileRequest r) {
    return s.update(u, r);
  }

  @PostMapping("/vehicles")
  @PreAuthorize("hasRole('DRIVER')")
  ResponseEntity<VehicleResponse> add(
      @AuthenticationPrincipal AuthenticatedUser u, @Valid @RequestBody VehicleRequest r) {
    return ResponseEntity.status(201).body(s.addVehicle(u, r));
  }

  @GetMapping("/vehicles")
  @PreAuthorize("hasRole('DRIVER')")
  List<VehicleResponse> vehicles(@AuthenticationPrincipal AuthenticatedUser u) {
    return s.vehicles(u);
  }

  @PutMapping("/vehicles/{id}")
  @PreAuthorize("hasRole('DRIVER')")
  VehicleResponse vehicle(
      @AuthenticationPrincipal AuthenticatedUser u,
      @PathVariable Long id,
      @Valid @RequestBody VehicleRequest r) {
    return s.updateVehicle(u, id, r);
  }

  @PatchMapping("/availability")
  @PreAuthorize("hasRole('DRIVER')")
  ProfileResponse available(
      @AuthenticationPrincipal AuthenticatedUser u, @RequestBody AvailabilityRequest r) {
    return s.availability(u, r);
  }

  @PutMapping("/location")
  @PreAuthorize("hasRole('DRIVER')")
  ProfileResponse location(
      @AuthenticationPrincipal AuthenticatedUser u, @Valid @RequestBody LocationRequest r) {
    return s.location(u, r);
  }

  @GetMapping("/eligible")
  @Operation(summary = "Get available drivers in deterministic creation order")
  List<ProfileResponse> eligible(
      @RequestParam @jakarta.validation.constraints.NotBlank String serviceArea) {
    return s.eligible(serviceArea);
  }
}
