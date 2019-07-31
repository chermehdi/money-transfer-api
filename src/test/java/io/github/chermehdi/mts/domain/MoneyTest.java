package io.github.chermehdi.mts.domain;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.valueOf;
import static java.util.Currency.getInstance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  public void testCanCreateMoneyObjectWithUnknownCurrencyThrows() {

    String unknownCurrencyCode = "ABCDEDEF";
    assertThrows(IllegalArgumentException.class, () -> {
          new Money(ONE, unknownCurrencyCode);
        }
    );
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
    var money1 = new Money(ONE, getInstance("MAD"));
    var money2 = new Money(ONE, getInstance("EUR"));
    assertThrows(ValidationException.class, () -> {
      money1.add(money2);
    });
  }

  @Test
  public void testCanAddMoneyWithSameCurrency() {
    var money1 = new Money(ONE, getInstance("MAD"));
    var money2 = new Money(valueOf(2), getInstance("MAD"));
    Money result = money1.add(money2);
    // money objects do not change after operation
    assertEquals(valueOf(1), money1.getAmount());
    assertEquals(valueOf(2), money2.getAmount());

    assertEquals(valueOf(2).add(ONE), result.getAmount());
  }

  @Test
  public void testIsPositive() {
    var positiveMoney = new Money(valueOf(10));
    assertTrue(positiveMoney.isPositive());
    var zeroMoney = new Money(BigDecimal.ZERO);
    assertTrue(zeroMoney.isPositive());
    var negativeMoney = new Money(valueOf(-100));

    assertFalse(negativeMoney.isPositive());
  }

  @Test
  public void testIsBiggerThanThrowsWhenDifferentCurrencies() {
    assertThrows(ValidationException.class, () -> {
      var money1 = new Money(valueOf(100L), "EUR");
      var money2 = new Money(valueOf(10L), "MAD");
      money1.isBiggerThan(money2);
    });
  }

  @Test
  public void testIsBiggerThan() {
    var money1 = new Money(valueOf(100L), "EUR");
    var money2 = new Money(valueOf(10L), "EUR");
    assertTrue(money1.isBiggerThan(money2));
  }

  @Test
  public void testSubtract() {
    var money1 = new Money(valueOf(100L), "EUR");
    var money2 = new Money(valueOf(10L), "EUR");
    var result = money1.subtract(money2);
    assertEquals(BigDecimal.valueOf(90L).setScale(Money.DEFAULT_COMPARISON_SCALE),
        result.getAmount().setScale(Money.DEFAULT_COMPARISON_SCALE));
  }

  @Test
  public void testIsZeroMoney() {
    var zeroMoney = new Money(valueOf(0L));
    var noneZeroMoney = new Money(valueOf(1L));

    assertTrue(zeroMoney.isZero());
    assertFalse(noneZeroMoney.isZero());
  }
}