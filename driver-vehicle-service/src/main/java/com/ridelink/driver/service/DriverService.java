package com.ridelink.driver.service;

import static com.ridelink.driver.exception.ExceptionSupport.*;

import com.ridelink.driver.dto.DriverDtos.*;
import com.ridelink.driver.entity.*;
import com.ridelink.driver.repository.*;
import com.ridelink.driver.security.SecuritySupport.AuthenticatedUser;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DriverService {
  private final DriverProfileRepository profiles;
  private final VehicleRepository vehicles;

  public DriverService(DriverProfileRepository p, VehicleRepository v) {
    profiles = p;
    vehicles = v;
  }

  public ProfileResponse create(AuthenticatedUser u, ProfileRequest r) {
    if (profiles.findByAccountId(u.accountId()).isPresent())
      throw new DuplicateResourceException("Driver profile already exists");
    DriverProfile p = new DriverProfile();
    p.setAccountId(u.accountId());
    p.setServiceArea(r.serviceArea().trim());
    return map(profiles.save(p));
  }

  @Transactional(readOnly = true)
  public ProfileResponse me(AuthenticatedUser u) {
    return map(find(u.accountId()));
  }

  public ProfileResponse update(AuthenticatedUser u, ProfileRequest r) {
    DriverProfile p = find(u.accountId());
    p.setServiceArea(r.serviceArea().trim());
    return map(profiles.save(p));
  }

  public ProfileResponse availability(AuthenticatedUser u, AvailabilityRequest r) {
    DriverProfile p = find(u.accountId());
    p.setAvailable(r.available());
    return map(profiles.save(p));
  }

  public ProfileResponse location(AuthenticatedUser u, LocationRequest r) {
    DriverProfile p = find(u.accountId());
    p.setLatitude(r.latitude());
    p.setLongitude(r.longitude());
    return map(profiles.save(p));
  }

  public VehicleResponse addVehicle(AuthenticatedUser u, VehicleRequest r) {
    DriverProfile p = find(u.accountId());
    Vehicle v = new Vehicle();
    v.setDriverProfileId(p.getId());
    copy(v, r);
    return map(vehicles.save(v));
  }

  @Transactional(readOnly = true)
  public List<VehicleResponse> vehicles(AuthenticatedUser u) {
    return vehicles.findByDriverProfileIdOrderByCreatedAtAsc(find(u.accountId()).getId()).stream()
        .map(this::map)
        .toList();
  }

  public VehicleResponse updateVehicle(AuthenticatedUser u, Long id, VehicleRequest r) {
    DriverProfile p = find(u.accountId());
    Vehicle v =
        vehicles
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + id));
    if (!v.getDriverProfileId().equals(p.getId()))
      throw new UnauthorizedOperationException("Vehicle belongs to another driver");
    copy(v, r);
    return map(vehicles.save(v));
  }

  @Transactional(readOnly = true)
  public List<ProfileResponse> eligible(String area) {
    return profiles.findByAvailableTrueAndServiceAreaIgnoreCaseOrderByCreatedAtAsc(area).stream()
        .map(this::map)
        .toList();
  }

  private DriverProfile find(Long account) {
    return profiles
        .findByAccountId(account)
        .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found"));
  }

  private void copy(Vehicle v, VehicleRequest r) {
    v.setRegistrationNumber(r.registrationNumber().trim().toUpperCase());
    v.setMake(r.make().trim());
    v.setModel(r.model().trim());
    v.setColor(r.color().trim());
    v.setVehicleType(r.vehicleType());
  }

  private ProfileResponse map(DriverProfile p) {
    return new ProfileResponse(
        p.getId(),
        p.getAccountId(),
        p.isAvailable(),
        p.getServiceArea(),
        p.getLatitude(),
        p.getLongitude(),
        p.getCreatedAt(),
        p.getUpdatedAt());
  }

  private VehicleResponse map(Vehicle v) {
    return new VehicleResponse(
        v.getId(),
        v.getDriverProfileId(),
        v.getRegistrationNumber(),
        v.getMake(),
        v.getModel(),
        v.getColor(),
        v.getVehicleType(),
        v.getCreatedAt(),
        v.getUpdatedAt());
  }
}
