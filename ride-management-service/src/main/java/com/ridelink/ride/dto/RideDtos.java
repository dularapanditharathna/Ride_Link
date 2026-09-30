package com.ridelink.ride.dto;

import com.ridelink.ride.entity.RideStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public final class RideDtos {
  private RideDtos() {}

  public record CreateRideRequest(
      @NotBlank String pickup,
      @NotBlank String destination,
      @NotBlank String serviceArea,
      @NotNull @Positive BigDecimal distanceKm,
      @PositiveOrZero BigDecimal estimatedFare) {}

  public record RideResponse(
      Long id,
      Long passengerAccountId,
      Long driverAccountId,
      String pickup,
      String destination,
      String serviceArea,
      BigDecimal distanceKm,
      RideStatus status,
      BigDecimal estimatedFare,
      BigDecimal finalFare,
      Instant createdAt,
      Instant acceptedAt,
      Instant startedAt,
      Instant completedAt,
      Instant cancelledAt) {}

  public record EligibleDriver(
      Long id,
      Long accountId,
      boolean available,
      String serviceArea,
      Double latitude,
      Double longitude,
      Instant createdAt,
      Instant updatedAt) {}

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
      String type,
      Instant createdAt) {}
}
