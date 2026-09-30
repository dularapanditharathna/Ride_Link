package com.ridelink.driver.repository;

import com.ridelink.driver.entity.Vehicle;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
  List<Vehicle> findByDriverProfileIdOrderByCreatedAtAsc(Long id);
}
