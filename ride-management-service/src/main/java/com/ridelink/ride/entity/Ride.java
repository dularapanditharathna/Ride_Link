package com.ridelink.ride.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "rides")
public class Ride {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long passengerAccountId;

  private Long driverAccountId;

  @Column(nullable = false)
  private String pickup;

  @Column(nullable = false)
  private String destination;

  @Column(nullable = false)
  private String serviceArea;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal distanceKm;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RideStatus status;

  @Column(precision = 12, scale = 2)
  private BigDecimal estimatedFare;

  @Column(precision = 12, scale = 2)
  private BigDecimal finalFare;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  private Instant acceptedAt;
  private Instant startedAt;
  private Instant completedAt;
  private Instant cancelledAt;

  @PrePersist
  void create() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long v) {
    id = v;
  }

  public Long getPassengerAccountId() {
    return passengerAccountId;
  }

  public void setPassengerAccountId(Long v) {
    passengerAccountId = v;
  }

  public Long getDriverAccountId() {
    return driverAccountId;
  }

  public void setDriverAccountId(Long v) {
    driverAccountId = v;
  }

  public String getPickup() {
    return pickup;
  }

  public void setPickup(String v) {
    pickup = v;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String v) {
    destination = v;
  }

  public String getServiceArea() {
    return serviceArea;
  }

  public void setServiceArea(String v) {
    serviceArea = v;
  }

  public BigDecimal getDistanceKm() {
    return distanceKm;
  }

  public void setDistanceKm(BigDecimal v) {
    distanceKm = v;
  }

  public RideStatus getStatus() {
    return status;
  }

  public void setStatus(RideStatus v) {
    status = v;
  }

  public BigDecimal getEstimatedFare() {
    return estimatedFare;
  }

  public void setEstimatedFare(BigDecimal v) {
    estimatedFare = v;
  }

  public BigDecimal getFinalFare() {
    return finalFare;
  }

  public void setFinalFare(BigDecimal v) {
    finalFare = v;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getAcceptedAt() {
    return acceptedAt;
  }

  public void setAcceptedAt(Instant v) {
    acceptedAt = v;
  }

  public Instant getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(Instant v) {
    startedAt = v;
  }

  public Instant getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(Instant v) {
    completedAt = v;
  }

  public Instant getCancelledAt() {
    return cancelledAt;
  }

  public void setCancelledAt(Instant v) {
    cancelledAt = v;
  }
}
