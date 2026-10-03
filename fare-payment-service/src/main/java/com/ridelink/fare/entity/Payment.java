package com.ridelink.fare.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
    name = "payments",
    uniqueConstraints =
        @UniqueConstraint(name = "uk_payment_receipt", columnNames = "receiptNumber"))
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long rideId;

  @Column(nullable = false)
  private Long passengerAccountId;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false)
  private String paymentMethod;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus status;

  @Column(nullable = false)
  private String receiptNumber;

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

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal v) {
    amount = v;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String v) {
    paymentMethod = v;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus v) {
    status = v;
  }

  public String getReceiptNumber() {
    return receiptNumber;
  }

  public void setReceiptNumber(String v) {
    receiptNumber = v;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
