package com.ridelink.driver.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ridelink.driver.entity.DriverProfile;
import com.ridelink.driver.entity.Vehicle;
import com.ridelink.driver.entity.VehicleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class NumericIdPersistenceTest {
  @Autowired private DriverProfileRepository profiles;
  @Autowired private VehicleRepository vehicles;

  @Test
  void profilesAndVehiclesReceiveSequentialNumericIds() {
    Long firstProfile = profiles.save(profile(1L)).getId();
    Long secondProfile = profiles.save(profile(2L)).getId();
    assertEquals(firstProfile + 1, secondProfile);

    Long firstVehicle = vehicles.save(vehicle(firstProfile, "WP-1001")).getId();
    Long secondVehicle = vehicles.save(vehicle(secondProfile, "WP-1002")).getId();
    assertEquals(firstVehicle + 1, secondVehicle);
  }

  private DriverProfile profile(Long accountId) {
    DriverProfile profile = new DriverProfile();
    profile.setAccountId(accountId);
    profile.setServiceArea("Colombo");
    profile.setAvailable(true);
    return profile;
  }

  private Vehicle vehicle(Long profileId, String registration) {
    Vehicle vehicle = new Vehicle();
    vehicle.setDriverProfileId(profileId);
    vehicle.setRegistrationNumber(registration);
    vehicle.setMake("Toyota");
    vehicle.setModel("Aqua");
    vehicle.setColor("White");
    vehicle.setVehicleType(VehicleType.CAR);
    return vehicle;
  }
}
