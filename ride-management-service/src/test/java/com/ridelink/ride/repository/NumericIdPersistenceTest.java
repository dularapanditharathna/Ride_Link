package com.ridelink.ride.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ridelink.ride.entity.Ride;
import com.ridelink.ride.entity.RideStatus;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class NumericIdPersistenceTest {
  @Autowired private RideRepository rides;

  @Test
  void ridesReceiveSequentialNumericIds() {
    Long first = rides.save(ride()).getId();
    Long second = rides.save(ride()).getId();
    assertEquals(first + 1, second);
  }

  private Ride ride() {
    Ride ride = new Ride();
    ride.setPassengerAccountId(1L);
    ride.setPickup("A");
    ride.setDestination("B");
    ride.setServiceArea("Colombo");
    ride.setDistanceKm(new BigDecimal("5.00"));
    ride.setStatus(RideStatus.REQUESTED);
    return ride;
  }
}
