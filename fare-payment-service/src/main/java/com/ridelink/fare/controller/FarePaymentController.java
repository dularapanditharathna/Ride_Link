package com.ridelink.fare.controller;

import com.ridelink.fare.dto.FareDtos.*;
import com.ridelink.fare.security.SecuritySupport.AuthenticatedUser;
import com.ridelink.fare.service.FarePaymentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class FarePaymentController {
  private final FarePaymentService s;

  public FarePaymentController(FarePaymentService s) {
    this.s = s;
  }

  @PostMapping("/api/fares/estimate")
  @PreAuthorize("hasRole('PASSENGER')")
  @Operation(summary = "Calculate and store a fare estimate")
  ResponseEntity<FareResponse> estimate(
      @Valid @RequestBody EstimateRequest r, @AuthenticationPrincipal AuthenticatedUser u) {
    return ResponseEntity.status(201).body(s.estimate(r, u));
  }

  @PostMapping("/api/fares/final")
  @PreAuthorize("hasAnyRole('DRIVER','ADMIN')")
  @Operation(summary = "Calculate and store the final fare for a completed ride")
  ResponseEntity<FareResponse> finalFare(@Valid @RequestBody FinalFareRequest r) {
    return ResponseEntity.status(201).body(s.finalFare(r));
  }

  @PostMapping("/api/payments")
  @PreAuthorize("hasRole('PASSENGER')")
  @Operation(summary = "Run a simulated payment; set simulateFailure=true for the failure path")
  ResponseEntity<PaymentResponse> pay(
      @Valid @RequestBody PaymentRequest r, @AuthenticationPrincipal AuthenticatedUser u) {
    return ResponseEntity.status(201).body(s.pay(r, u));
  }

  @GetMapping("/api/payments/{id}")
  PaymentResponse get(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser u) {
    return s.payment(id, u);
  }

  @GetMapping("/api/payments/{id}/receipt")
  ReceiptResponse receipt(@PathVariable Long id, @AuthenticationPrincipal AuthenticatedUser u) {
    return s.receipt(id, u);
  }
}
