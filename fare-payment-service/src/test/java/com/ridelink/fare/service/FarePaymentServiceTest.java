package com.ridelink.fare.service;

import static com.ridelink.fare.exception.ExceptionSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ridelink.fare.dto.FareDtos.*;
import com.ridelink.fare.entity.*;
import com.ridelink.fare.repository.*;
import com.ridelink.fare.security.SecuritySupport.AuthenticatedUser;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FarePaymentServiceTest {
  @Mock FareRecordRepository fares;
  @Mock PaymentRepository payments;
  FarePaymentService service;
  Long passenger = 1L;

  AuthenticatedUser user() {
    return new AuthenticatedUser(passenger, "p@x", "PASSENGER");
  }

  @BeforeEach
  void init() {
    service = new FarePaymentService(fares, payments, new BigDecimal("300"), new BigDecimal("100"));
    lenient().when(fares.save(any())).thenAnswer(i -> i.getArgument(0));
    lenient()
        .when(payments.save(any()))
        .thenAnswer(
            i -> {
              Payment p = i.getArgument(0);
              if (p.getId() == null) p.setId(1L);
              return p;
            });
  }

  @Test
  void fareEstimate_success() {
    assertEquals(
        new BigDecimal("1300.00"),
        service.estimate(new EstimateRequest("A", "B", BigDecimal.TEN), user()).amount());
  }

  @Test
  void finalFare_success() {
    assertEquals(
        FareType.FINAL,
        service.finalFare(new FinalFareRequest(1L, BigDecimal.ONE, passenger, "A", "B")).type());
  }

  @Test
  void paymentSuccess() {
    assertEquals(
        PaymentStatus.SUCCESS,
        service.pay(new PaymentRequest(1L, BigDecimal.TEN, "CASH", false), user()).status());
  }

  @Test
  void paymentFailure() {
    assertThrows(
        PaymentFailedException.class,
        () -> service.pay(new PaymentRequest(1L, BigDecimal.TEN, "CARD", true), user()));
  }

  @Test
  void receiptGeneration_success() {
    Payment p = new Payment();
    p.setId(1L);
    p.setRideId(1L);
    p.setPassengerAccountId(passenger);
    p.setAmount(BigDecimal.TEN);
    p.setPaymentMethod("CASH");
    p.setStatus(PaymentStatus.SUCCESS);
    p.setReceiptNumber("RL-TEST");
    when(payments.findById(p.getId())).thenReturn(Optional.of(p));
    assertEquals("RL-TEST", service.receipt(p.getId(), user()).receiptNumber());
  }
}
