package io.github.chermehdi.mts.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.chermehdi.mts.util.validation.ValidationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * @author chermehdi
 */
class AccountTest {

  @Test
  void testCanCreateAnAccount() {
    var account = new Account();
  }

  @Test
  void testCannotCreateAccountWithNegativeBalance() {
    var ex = assertThrows(ValidationException.class,
        () -> new Account(1, new Money(BigDecimal.valueOf(-1.0))));
    assertEquals("Cannot create an account with negative balance", ex.getMessage());
  }

  @Test
  void testCannotCreateAccountWithNullBalance() {
    assertThrows(ValidationException.class, () -> new Account(1, null));
  }
}