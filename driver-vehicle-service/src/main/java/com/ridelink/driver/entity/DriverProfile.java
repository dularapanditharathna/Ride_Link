package com.ridelink.driver.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "driver_profiles",
    uniqueConstraints = @UniqueConstraint(name = "uk_driver_account", columnNames = "accountId"))
public class DriverProfile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long accountId;

  @Column(nullable = false)
  private boolean available;

  @Column(nullable = false)
  private String serviceArea;

  private Double latitude;
  private Double longitude;

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

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long v) {
    accountId = v;
  }

  public boolean isAvailable() {
    return available;
  }

  public void setAvailable(boolean v) {
    available = v;
  }

  public String getServiceArea() {
    return serviceArea;
  }

  public void setServiceArea(String v) {
    serviceArea = v;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double v) {
    latitude = v;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double v) {
    longitude = v;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
