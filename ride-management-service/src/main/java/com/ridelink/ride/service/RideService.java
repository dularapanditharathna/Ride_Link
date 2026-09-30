package com.ridelink.ride.service;

import static com.ridelink.ride.exception.ExceptionSupport.*;

import com.ridelink.ride.client.*;
import com.ridelink.ride.dto.RideDtos.*;
import com.ridelink.ride.entity.*;
import com.ridelink.ride.repository.RideRepository;
import com.ridelink.ride.security.SecuritySupport.AuthenticatedUser;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RideService {
  private final RideRepository repo;
  private final DriverServiceClient drivers;
  private final FareServiceClient fares;

  public RideService(RideRepository r, DriverServiceClient d, FareServiceClient f) {
    repo = r;
    drivers = d;
    fares = f;
  }

  public RideResponse create(AuthenticatedUser u, CreateRideRequest q, String auth) {
    Ride r = new Ride();
    r.setPassengerAccountId(u.accountId());
    r.setPickup(q.pickup().trim());
    r.setDestination(q.destination().trim());
    r.setServiceArea(q.serviceArea().trim());
    r.setDistanceKm(q.distanceKm());
    r.setEstimatedFare(q.estimatedFare());
    r.setStatus(RideStatus.REQUESTED);
    List<EligibleDriver> found = drivers.eligible(q.serviceArea(), auth);
    if (found == null || found.isEmpty())
      throw new NoAvailableDriverException("No available drivers in the requested service area");
    r.setDriverAccountId(found.get(0).accountId());
    r.setStatus(RideStatus.ASSIGNED);
    return map(repo.save(r));
  }

  @Transactional(readOnly = true)
  public RideResponse get(Long id, AuthenticatedUser u) {
    Ride r = find(id);
    canView(r, u);
    return map(r);
  }

  @Transactional(readOnly = true)
  public List<RideResponse> mine(AuthenticatedUser u) {
    return repo
        .findByPassengerAccountIdOrDriverAccountIdOrderByCreatedAtDesc(u.accountId(), u.accountId())
        .stream()
        .map(this::map)
        .toList();
  }

  public RideResponse accept(Long id, AuthenticatedUser u) {
    Ride r = assigned(id, u);
    require(r, RideStatus.ASSIGNED, RideStatus.ACCEPTED);
    r.setStatus(RideStatus.ACCEPTED);
    r.setAcceptedAt(Instant.now());
    return map(repo.save(r));
  }

  public RideResponse start(Long id, AuthenticatedUser u) {
    Ride r = assigned(id, u);
    require(r, RideStatus.ACCEPTED, RideStatus.IN_PROGRESS);
    r.setStatus(RideStatus.IN_PROGRESS);
    r.setStartedAt(Instant.now());
    return map(repo.save(r));
  }

  public RideResponse complete(Long id, AuthenticatedUser u, String auth) {
    Ride r = assigned(id, u);
    require(r, RideStatus.IN_PROGRESS, RideStatus.COMPLETED);
    FareResponse fare =
        fares.finalFare(
            new FinalFareRequest(
                r.getId(),
                r.getDistanceKm(),
                r.getPassengerAccountId(),
                r.getPickup(),
                r.getDestination()),
            auth);
    r.setFinalFare(fare.amount());
    r.setStatus(RideStatus.COMPLETED);
    r.setCompletedAt(Instant.now());
    return map(repo.save(r));
  }

  public RideResponse cancel(Long id, AuthenticatedUser u) {
    Ride r = find(id);
    if (!u.accountId().equals(r.getPassengerAccountId())
        && !u.accountId().equals(r.getDriverAccountId()))
      throw new UnauthorizedOperationException(
          "Only the passenger or assigned driver can cancel this ride");
    if (!EnumSet.of(RideStatus.REQUESTED, RideStatus.ASSIGNED, RideStatus.ACCEPTED)
        .contains(r.getStatus()))
      throw new InvalidStatusTransitionException(
          "Ride in status " + r.getStatus() + " cannot be cancelled");
    r.setStatus(RideStatus.CANCELLED);
    r.setCancelledAt(Instant.now());
    return map(repo.save(r));
  }

  private Ride assigned(Long id, AuthenticatedUser u) {
    Ride r = find(id);
    if (!u.accountId().equals(r.getDriverAccountId()))
      throw new UnauthorizedOperationException(
          "Only the assigned driver can perform this operation");
    return r;
  }

  private void require(Ride r, RideStatus from, RideStatus to) {
    if (r.getStatus() != from)
      throw new InvalidStatusTransitionException(
          "Cannot transition ride from " + r.getStatus() + " to " + to);
  }

  private Ride find(Long id) {
    return repo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Ride not found: " + id));
  }

  private void canView(Ride r, AuthenticatedUser u) {
    if (!u.accountId().equals(r.getPassengerAccountId())
        && !u.accountId().equals(r.getDriverAccountId())
        && !"ADMIN".equals(u.role()))
      throw new UnauthorizedOperationException("You cannot view this ride");
  }

  private RideResponse map(Ride r) {
    return new RideResponse(
        r.getId(),
        r.getPassengerAccountId(),
        r.getDriverAccountId(),
        r.getPickup(),
        r.getDestination(),
        r.getServiceArea(),
        r.getDistanceKm(),
        r.getStatus(),
        r.getEstimatedFare(),
        r.getFinalFare(),
        r.getCreatedAt(),
        r.getAcceptedAt(),
        r.getStartedAt(),
        r.getCompletedAt(),
        r.getCancelledAt());
  }
}
