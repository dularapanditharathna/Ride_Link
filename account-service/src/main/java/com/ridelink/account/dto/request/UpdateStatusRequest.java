package com.ridelink.account.dto.request;

import com.ridelink.account.entity.AccountStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(@NotNull AccountStatus status) {}
