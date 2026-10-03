package com.ridelink.fare.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ridelink.fare.entity.FareRecord;
import com.ridelink.fare.entity.FareType;
import com.ridelink.fare.entity.Payment;
import com.ridelink.fare.entity.PaymentStatus;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class NumericIdPersistenceTest {
  @Autowired private FareRecordRepository fares;
  @Autowired private PaymentRepository payments;

  @Test
  void faresAndPaymentsReceiveSequentialNumericIds() {
    Long firstFare = fares.save(fare(1L)).getId();
    Long secondFare = fares.save(fare(2L)).getId();
    assertEquals(firstFare + 1, secondFare);

    Long firstPayment = payments.save(payment(1L)).getId();
    Long secondPayment = payments.save(payment(2L)).getId();
    assertEquals(firstPayment + 1, secondPayment);
  }

  private FareRecord fare(Long rideId) {
    FareRecord fare = new FareRecord();
    fare.setRideId(rideId);
    fare.setPassengerAccountId(1L);
    fare.setPickup("A");
    fare.setDestination("B");
    fare.setDistanceKm(new BigDecimal("5.00"));
    fare.setAmount(new BigDecimal("800.00"));
    fare.setType(FareType.FINAL);
    return fare;
  }

  private Payment payment(Long rideId) {
    Payment payment = new Payment();
    payment.setRideId(rideId);
    payment.setPassengerAccountId(1L);
    payment.setAmount(new BigDecimal("800.00"));
    payment.setPaymentMethod("CARD");
    payment.setStatus(PaymentStatus.SUCCESS);
    payment.setReceiptNumber("TEST-" + rideId);
    return payment;
  }
}
