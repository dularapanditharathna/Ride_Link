package com.ridelink.account.dto.request;

import jakarta.validation.constraints.*;

public record UpdateAccountRequest(
    @NotBlank @Size(max = 80) String firstName,
    @NotBlank @Size(max = 80) String lastName,
    @NotBlank @Size(min = 7, max = 30) String phone) {}
