package com.ridelink.driver.dto;

import com.ridelink.driver.entity.VehicleType;
import jakarta.validation.constraints.*;
import java.time.Instant;

public final class DriverDtos {
  private DriverDtos() {}

  public record ProfileRequest(@NotBlank @Size(max = 100) String serviceArea) {}

  public record AvailabilityRequest(boolean available) {}

  public record LocationRequest(
      @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
      @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude) {}

  public record ProfileResponse(
      Long id,
      Long accountId,
      boolean available,
      String serviceArea,
      Double latitude,
      Double longitude,
      Instant createdAt,
      Instant updatedAt) {}

  public record VehicleRequest(
      @NotBlank String registrationNumber,
      @NotBlank String make,
      @NotBlank String model,
      @NotBlank String color,
      @NotNull VehicleType vehicleType) {}

  public record VehicleResponse(
      Long id,
      Long driverProfileId,
      String registrationNumber,
      String make,
      String model,
      String color,
      VehicleType vehicleType,
      Instant createdAt,
      Instant updatedAt) {}
}
