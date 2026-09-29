package com.ridelink.account.dto.response;

import com.ridelink.account.entity.Role;

public record LoginResponse(String token, Long accountId, String email, Role role) {}
