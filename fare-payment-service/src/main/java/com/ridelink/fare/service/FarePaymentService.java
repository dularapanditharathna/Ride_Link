package com.ridelink.fare.service;

import static com.ridelink.fare.exception.ExceptionSupport.*;

import com.ridelink.fare.dto.FareDtos.*;
import com.ridelink.fare.entity.*;
import com.ridelink.fare.repository.*;
import com.ridelink.fare.security.SecuritySupport.AuthenticatedUser;
import java.math.*;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FarePaymentService {
  private final FareRecordRepository fares;
  private final PaymentRepository payments;
  private final BigDecimal base, perKm;

  public FarePaymentService(
      FareRecordRepository f,
      PaymentRepository p,
      @Value("${ridelink.fare.base}") BigDecimal b,
      @Value("${ridelink.fare.per-km}") BigDecimal k) {
    fares = f;
    payments = p;
    base = b;
    perKm = k;
  }

  public FareResponse estimate(EstimateRequest q, AuthenticatedUser u) {
    return save(
        null, u.accountId(), q.pickup(), q.destination(), q.distanceKm(), FareType.ESTIMATE);
  }

  public FareResponse finalFare(FinalFareRequest q) {
    return save(
        q.rideId(),
        q.passengerAccountId(),
        q.pickup(),
        q.destination(),
        q.distanceKm(),
        FareType.FINAL);
  }

  private FareResponse save(
      Long ride,
      Long passenger,
      String pickup,
      String destination,
      BigDecimal distance,
      FareType type) {
    FareRecord f = new FareRecord();
    f.setRideId(ride);
    f.setPassengerAccountId(passenger);
    f.setPickup(pickup.trim());
    f.setDestination(destination.trim());
    f.setDistanceKm(distance);
    f.setAmount(base.add(distance.multiply(perKm)).setScale(2, RoundingMode.HALF_UP));
    f.setType(type);
    return map(fares.save(f));
  }

  @Transactional(noRollbackFor = PaymentFailedException.class)
  public PaymentResponse pay(PaymentRequest q, AuthenticatedUser u) {
    Payment p = new Payment();
    p.setRideId(q.rideId());
    p.setPassengerAccountId(u.accountId());
    p.setAmount(q.amount().setScale(2, RoundingMode.HALF_UP));
    p.setPaymentMethod(q.paymentMethod().trim().toUpperCase());
    p.setReceiptNumber(
        "RL-"
            + System.currentTimeMillis()
            + "-"
            + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    p.setStatus(q.simulateFailure() ? PaymentStatus.FAILED : PaymentStatus.SUCCESS);
    p = payments.save(p);
    if (q.simulateFailure())
      throw new PaymentFailedException("Simulated payment failure. Payment ID: " + p.getId());
    return map(p);
  }

  @Transactional(readOnly = true)
  public PaymentResponse payment(Long id, AuthenticatedUser u) {
    return map(owned(id, u));
  }

  @Transactional(readOnly = true)
  public ReceiptResponse receipt(Long id, AuthenticatedUser u) {
    Payment p = owned(id, u);
    if (p.getStatus() != PaymentStatus.SUCCESS)
      throw new PaymentFailedException("A receipt is only available for successful payments");
    return new ReceiptResponse(
        p.getReceiptNumber(),
        p.getId(),
        p.getRideId(),
        p.getAmount(),
        p.getPaymentMethod(),
        p.getStatus(),
        p.getCreatedAt());
  }

  private Payment owned(Long id, AuthenticatedUser u) {
    Payment p =
        payments
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
    if (!p.getPassengerAccountId().equals(u.accountId()) && !"ADMIN".equals(u.role()))
      throw new UnauthorizedOperationException("You cannot view this payment");
    return p;
  }

  private FareResponse map(FareRecord f) {
    return new FareResponse(
        f.getId(),
        f.getRideId(),
        f.getPassengerAccountId(),
        f.getAmount(),
        f.getType(),
        f.getCreatedAt());
  }

  private PaymentResponse map(Payment p) {
    return new PaymentResponse(
        p.getId(),
        p.getRideId(),
        p.getPassengerAccountId(),
        p.getAmount(),
        p.getPaymentMethod(),
        p.getStatus(),
        p.getReceiptNumber(),
        p.getCreatedAt());
  }
}
