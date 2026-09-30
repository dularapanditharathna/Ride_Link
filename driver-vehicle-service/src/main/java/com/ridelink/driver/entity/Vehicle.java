package com.ridelink.driver.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "vehicles",
    uniqueConstraints =
        @UniqueConstraint(name = "uk_vehicle_registration", columnNames = "registrationNumber"))
public class Vehicle {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long driverProfileId;

  @Column(nullable = false)
  private String registrationNumber;

  @Column(nullable = false)
  private String make;

  @Column(nullable = false)
  private String model;

  @Column(nullable = false)
  private String color;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private VehicleType vehicleType;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private Instant updatedAt;

  @PrePersist
  void create() {
    createdAt = updatedAt = Instant.now();
  }

  @PreUpdate
  void update() {
    updatedAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long v) {
    id = v;
  }

  public Long getDriverProfileId() {
    return driverProfileId;
  }

  public void setDriverProfileId(Long v) {
    driverProfileId = v;
  }

  public String getRegistrationNumber() {
    return registrationNumber;
  }

  public void setRegistrationNumber(String v) {
    registrationNumber = v;
  }

  public String getMake() {
    return make;
  }

  public void setMake(String v) {
    make = v;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String v) {
    model = v;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String v) {
    color = v;
  }

  public VehicleType getVehicleType() {
    return vehicleType;
  }

  public void setVehicleType(VehicleType v) {
    vehicleType = v;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
