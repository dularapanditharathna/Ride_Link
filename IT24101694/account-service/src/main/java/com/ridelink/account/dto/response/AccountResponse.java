package com.ridelink.account.dto.response;

import com.ridelink.account.entity.*;
import java.time.Instant;

public record AccountResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phone,
    Role role,
    AccountStatus status,
    Instant createdAt,
    Instant updatedAt) {}
