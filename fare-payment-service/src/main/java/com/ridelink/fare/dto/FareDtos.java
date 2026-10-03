package com.ridelink.fare.dto;

import com.ridelink.fare.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public final class FareDtos {
  private FareDtos() {}

  public record EstimateRequest(
      @NotBlank String pickup,
      @NotBlank String destination,
      @NotNull @Positive BigDecimal distanceKm) {}

  public record FinalFareRequest(
      @NotNull Long rideId,
      @NotNull @Positive BigDecimal distanceKm,
      @NotNull Long passengerAccountId,
      @NotBlank String pickup,
      @NotBlank String destination) {}

  public record FareResponse(
      Long id,
      Long rideId,
      Long passengerAccountId,
      BigDecimal amount,
      FareType type,
      Instant createdAt) {}

  public record PaymentRequest(
      @NotNull Long rideId,
      @NotNull @Positive BigDecimal amount,
      @NotBlank String paymentMethod,
      boolean simulateFailure) {}

  public record PaymentResponse(
      Long id,
      Long rideId,
      Long passengerAccountId,
      BigDecimal amount,
      String paymentMethod,
      PaymentStatus status,
      String receiptNumber,
      Instant createdAt) {}

  public record ReceiptResponse(
      String receiptNumber,
      Long paymentId,
      Long rideId,
      BigDecimal amount,
      String paymentMethod,
      PaymentStatus status,
      Instant paidAt) {}
}
