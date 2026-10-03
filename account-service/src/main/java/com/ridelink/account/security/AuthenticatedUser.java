package com.ridelink.account.security;

public record AuthenticatedUser(Long accountId, String email, String role) {}
