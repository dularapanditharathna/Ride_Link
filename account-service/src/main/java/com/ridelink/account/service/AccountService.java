package com.ridelink.account.service;

import static com.ridelink.account.exception.DomainExceptions.*;

import com.ridelink.account.dto.request.*;
import com.ridelink.account.dto.response.*;
import com.ridelink.account.entity.*;
import com.ridelink.account.repository.AccountRepository;
import com.ridelink.account.security.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {
  private final AccountRepository repo;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  public AccountService(AccountRepository repo, PasswordEncoder encoder, JwtService jwt) {
    this.repo = repo;
    this.encoder = encoder;
    this.jwt = jwt;
  }

  public AccountResponse register(RegisterRequest r, Role role) {
    if (repo.existsByEmailIgnoreCase(r.email()))
      throw new DuplicateEmailException("An account with this email already exists");
    Account a = new Account();
    a.setFirstName(r.firstName().trim());
    a.setLastName(r.lastName().trim());
    a.setEmail(r.email().trim().toLowerCase());
    a.setPasswordHash(encoder.encode(r.password()));
    a.setPhone(r.phone().trim());
    a.setRole(role);
    a.setStatus(AccountStatus.ACTIVE);
    return map(repo.save(a));
  }

  @Transactional(readOnly = true)
  public LoginResponse login(LoginRequest r) {
    Account a =
        repo.findByEmailIgnoreCase(r.email())
            .orElseThrow(() -> new AuthenticationException("Invalid email or password"));
    if (!encoder.matches(r.password(), a.getPasswordHash()))
      throw new AuthenticationException("Invalid email or password");
    if (a.getStatus() == AccountStatus.SUSPENDED)
      throw new AuthenticationException("Account is suspended");
    return new LoginResponse(jwt.generate(a), a.getId(), a.getEmail(), a.getRole());
  }

  @Transactional(readOnly = true)
  public AccountResponse me(AuthenticatedUser u) {
    return map(find(u.accountId()));
  }

  public AccountResponse updateMe(AuthenticatedUser u, UpdateAccountRequest r) {
    Account a = find(u.accountId());
    a.setFirstName(r.firstName().trim());
    a.setLastName(r.lastName().trim());
    a.setPhone(r.phone().trim());
    return map(repo.save(a));
  }

  public AccountResponse updateStatus(Long id, UpdateStatusRequest r) {
    Account a = find(id);
    a.setStatus(r.status());
    return map(repo.save(a));
  }

  private Account find(Long id) {
    return repo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
  }

  private AccountResponse map(Account a) {
    return new AccountResponse(
        a.getId(),
        a.getFirstName(),
        a.getLastName(),
        a.getEmail(),
        a.getPhone(),
        a.getRole(),
        a.getStatus(),
        a.getCreatedAt(),
        a.getUpdatedAt());
  }
}
