package com.ridelink.driver.repository;

import com.ridelink.driver.entity.DriverProfile;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
  Optional<DriverProfile> findByAccountId(Long id);

  List<DriverProfile> findByAvailableTrueAndServiceAreaIgnoreCaseOrderByCreatedAtAsc(String area);
}
