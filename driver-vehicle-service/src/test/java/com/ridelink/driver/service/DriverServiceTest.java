package com.ridelink.driver.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ridelink.driver.dto.DriverDtos.*;
import com.ridelink.driver.entity.*;
import com.ridelink.driver.repository.*;
import com.ridelink.driver.security.SecuritySupport.AuthenticatedUser;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {
  @Mock DriverProfileRepository profiles;
  @Mock VehicleRepository vehicles;
  @InjectMocks DriverService service;
  final Long account = 2L;

  AuthenticatedUser user() {
    return new AuthenticatedUser(account, "d@x.com", "DRIVER");
  }

  DriverProfile profile() {
    DriverProfile p = new DriverProfile();
    p.setId(1L);
    p.setAccountId(account);
    p.setServiceArea("Negombo");
    return p;
  }

  @Test
  void createProfile_success() {
    when(profiles.save(any())).thenAnswer(i -> i.getArgument(0));
    assertEquals(account, service.create(user(), new ProfileRequest("Negombo")).accountId());
  }

  @Test
  void updateAvailability_success() {
    DriverProfile p = profile();
    when(profiles.findByAccountId(account)).thenReturn(Optional.of(p));
    when(profiles.save(p)).thenReturn(p);
    assertTrue(service.availability(user(), new AvailabilityRequest(true)).available());
  }

  @Test
  void eligibleDrivers_onlyReturnsAvailableMatchingArea() {
    DriverProfile p = profile();
    p.setAvailable(true);
    when(profiles.findByAvailableTrueAndServiceAreaIgnoreCaseOrderByCreatedAtAsc("Negombo"))
        .thenReturn(List.of(p));
    var result = service.eligible("Negombo");
    assertEquals(1, result.size());
    assertTrue(result.get(0).available());
  }
}
