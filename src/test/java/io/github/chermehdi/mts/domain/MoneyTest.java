package io.github.chermehdi.mts.domain;

import static java.math.BigDecimal.ONE;
import static java.util.Currency.getInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.chermehdi.mts.util.ConfigurationProvider;
import io.github.chermehdi.mts.util.validation.ValidationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * @author chermehdi
 */
class MoneyTest {

  @Test
  public void testCanCreateMoneyObject() {
    var money = new Money(ONE);
  }

  @Test
  public void testCreateMoneyWithDefaultCurrency() {
    var money = new Money(ONE);
    var defaultCurrencyName = new ConfigurationProvider(true).get()
        .getProperty("mts.default.currency");

    assertEquals(money.getCurrency().getCurrencyCode(), defaultCurrencyName);
  }

  @Test
  public void testCannotAddMoneyWithDifferentCurrency() {
    var money1 = new Money(getInstance("MAD"), ONE);
    var money2 = new Money(getInstance("EUR"), ONE);
    assertThrows(ValidationException.class, () -> {
      money1.add(money2);
    });
  }

  @Test
  public void testCanAddMoneyWithSameCurrency() {
    var money1 = new Money(getInstance("MAD"), ONE);
    var money2 = new Money(getInstance("MAD"), BigDecimal.valueOf(2));
    Money result = money1.add(money2);
    // money objects do not change after operation
    assertEquals(BigDecimal.valueOf(1), money1.getAmount());
    assertEquals(BigDecimal.valueOf(2), money2.getAmount());

    assertEquals(BigDecimal.valueOf(2).add(ONE), result.getAmount());
  }
}