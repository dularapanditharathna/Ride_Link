package com.ridelink.ride.service;

import static com.ridelink.ride.exception.ExceptionSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ridelink.ride.client.*;
import com.ridelink.ride.dto.RideDtos.*;
import com.ridelink.ride.entity.*;
import com.ridelink.ride.repository.RideRepository;
import com.ridelink.ride.security.SecuritySupport.AuthenticatedUser;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {
  @Mock RideRepository repo;
  @Mock DriverServiceClient drivers;
  @Mock FareServiceClient fares;
  @InjectMocks RideService service;
  Long passenger = 1L, driver = 2L;

  AuthenticatedUser p() {
    return new AuthenticatedUser(passenger, "p@x", "PASSENGER");
  }

  AuthenticatedUser d() {
    return new AuthenticatedUser(driver, "d@x", "DRIVER");
  }

  CreateRideRequest request() {
    return new CreateRideRequest("A", "B", "Negombo", BigDecimal.TEN, null);
  }

  Ride ride(RideStatus status) {
    Ride r = new Ride();
    r.setId(1L);
    r.setPassengerAccountId(passenger);
    r.setDriverAccountId(driver);
    r.setPickup("A");
    r.setDestination("B");
    r.setServiceArea("Negombo");
    r.setDistanceKm(BigDecimal.TEN);
    r.setStatus(status);
    return r;
  }

  @BeforeEach
  void save() {
    lenient().when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
  }

  @Test
  void createRideAndAssignDriver_success() {
    when(drivers.eligible(any(), any()))
        .thenReturn(
            List.of(new EligibleDriver(1L, driver, true, "Negombo", null, null, null, null)));
    assertEquals(RideStatus.ASSIGNED, service.create(p(), request(), "Bearer x").status());
  }

  @Test
  void noAvailableDriver_failure() {
    when(drivers.eligible(any(), any())).thenReturn(List.of());
    assertThrows(
        NoAvailableDriverException.class, () -> service.create(p(), request(), "Bearer x"));
  }

  @Test
  void assignedDriverAccept_success() {
    Ride r = ride(RideStatus.ASSIGNED);
    when(repo.findById(r.getId())).thenReturn(Optional.of(r));
    assertEquals(RideStatus.ACCEPTED, service.accept(r.getId(), d()).status());
  }

  @Test
  void differentDriverAccept_failure() {
    Ride r = ride(RideStatus.ASSIGNED);
    when(repo.findById(r.getId())).thenReturn(Optional.of(r));
    assertThrows(
        UnauthorizedOperationException.class,
        () -> service.accept(r.getId(), new AuthenticatedUser(3L, "x", "DRIVER")));
  }

  @Test
  void invalidStatusTransition_failure() {
    Ride r = ride(RideStatus.REQUESTED);
    when(repo.findById(r.getId())).thenReturn(Optional.of(r));
    assertThrows(InvalidStatusTransitionException.class, () -> service.accept(r.getId(), d()));
  }

  @Test
  void startRide_success() {
    Ride r = ride(RideStatus.ACCEPTED);
    when(repo.findById(r.getId())).thenReturn(Optional.of(r));
    assertEquals(RideStatus.IN_PROGRESS, service.start(r.getId(), d()).status());
  }

  @Test
  void completeRide_success() {
    Ride r = ride(RideStatus.IN_PROGRESS);
    when(repo.findById(r.getId())).thenReturn(Optional.of(r));
    when(fares.finalFare(any(), any()))
        .thenReturn(
            new FareResponse(1L, r.getId(), passenger, new BigDecimal("1300.00"), "FINAL", null));
    assertEquals(RideStatus.COMPLETED, service.complete(r.getId(), d(), "Bearer x").status());
  }
}
