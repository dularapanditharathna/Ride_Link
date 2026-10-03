package com.ridelink.fare.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "fare_records")
public class FareRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long rideId;

  @Column(nullable = false)
  private Long passengerAccountId;

  @Column(nullable = false)
  private String pickup;

  @Column(nullable = false)
  private String destination;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal distanceKm;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FareType type;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

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

  public Long getRideId() {
    return rideId;
  }

  public void setRideId(Long v) {
    rideId = v;
  }

  public Long getPassengerAccountId() {
    return passengerAccountId;
  }

  public void setPassengerAccountId(Long v) {
    passengerAccountId = v;
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

  public BigDecimal getDistanceKm() {
    return distanceKm;
  }

  public void setDistanceKm(BigDecimal v) {
    distanceKm = v;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal v) {
    amount = v;
  }

  public FareType getType() {
    return type;
  }

  public void setType(FareType v) {
    type = v;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
