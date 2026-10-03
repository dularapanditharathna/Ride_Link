package com.ridelink.account.service;

import static com.ridelink.account.exception.DomainExceptions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ridelink.account.dto.request.*;
import com.ridelink.account.entity.*;
import com.ridelink.account.repository.AccountRepository;
import com.ridelink.account.security.JwtService;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
  @Mock AccountRepository repo;
  @Mock PasswordEncoder encoder;
  @Mock JwtService jwt;
  @InjectMocks AccountService service;

  RegisterRequest req() {
    return new RegisterRequest("Pat", "One", "pat@example.com", "password1", "0771234567");
  }

  Account account(AccountStatus status) {
    Account a = new Account();
    a.setId(1L);
    a.setEmail("pat@example.com");
    a.setPasswordHash("hash");
    a.setRole(Role.PASSENGER);
    a.setStatus(status);
    a.setFirstName("Pat");
    a.setLastName("One");
    a.setPhone("0771234567");
    return a;
  }

  @Test
  void registerPassenger_success() {
    when(encoder.encode(any())).thenReturn("hash");
    when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
    assertEquals(Role.PASSENGER, service.register(req(), Role.PASSENGER).role());
    verify(repo).save(any());
  }

  @Test
  void duplicateEmail_failure() {
    when(repo.existsByEmailIgnoreCase(any())).thenReturn(true);
    assertThrows(DuplicateEmailException.class, () -> service.register(req(), Role.PASSENGER));
  }

  @Test
  void loginValidCredentials_success() {
    Account a = account(AccountStatus.ACTIVE);
    when(repo.findByEmailIgnoreCase(any())).thenReturn(Optional.of(a));
    when(encoder.matches("password1", "hash")).thenReturn(true);
    when(jwt.generate(a)).thenReturn("token");
    assertEquals("token", service.login(new LoginRequest(a.getEmail(), "password1")).token());
  }

  @Test
  void invalidPassword_failure() {
    Account a = account(AccountStatus.ACTIVE);
    when(repo.findByEmailIgnoreCase(any())).thenReturn(Optional.of(a));
    assertThrows(
        AuthenticationException.class, () -> service.login(new LoginRequest(a.getEmail(), "bad")));
  }

  @Test
  void suspendedAccountLogin_failure() {
    Account a = account(AccountStatus.SUSPENDED);
    when(repo.findByEmailIgnoreCase(any())).thenReturn(Optional.of(a));
    when(encoder.matches(any(), any())).thenReturn(true);
    assertThrows(
        AuthenticationException.class,
        () -> service.login(new LoginRequest(a.getEmail(), "password1")));
  }
}
