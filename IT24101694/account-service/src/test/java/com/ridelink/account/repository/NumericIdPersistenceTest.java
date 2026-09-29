package com.ridelink.account.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ridelink.account.entity.Account;
import com.ridelink.account.entity.AccountStatus;
import com.ridelink.account.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class NumericIdPersistenceTest {
  @Autowired private AccountRepository accounts;

  @Test
  void accountsReceiveSequentialNumericIds() {
    Long first = accounts.save(account("first@ridelink.test")).getId();
    Long second = accounts.save(account("second@ridelink.test")).getId();
    assertEquals(first + 1, second);
  }

  private Account account(String email) {
    Account account = new Account();
    account.setFirstName("Test");
    account.setLastName("Passenger");
    account.setEmail(email);
    account.setPasswordHash("test-hash");
    account.setPhone("0771234567");
    account.setRole(Role.PASSENGER);
    account.setStatus(AccountStatus.ACTIVE);
    return account;
  }
}
