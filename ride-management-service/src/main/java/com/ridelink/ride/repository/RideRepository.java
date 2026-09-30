package com.ridelink.ride.repository;

import com.ridelink.ride.entity.Ride;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride, Long> {
  List<Ride> findByPassengerAccountIdOrDriverAccountIdOrderByCreatedAtDesc(
      Long passenger, Long driver);
}
