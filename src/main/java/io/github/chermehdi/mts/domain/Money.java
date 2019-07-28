package io.github.chermehdi.mts.domain;

import io.github.chermehdi.mts.util.ConfigurationProvider;
import io.github.chermehdi.mts.util.validation.Validation;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * @author chermehdi
 */
public class Money {

  private final Currency currency;

  private final BigDecimal amount;

  public static final Currency DEFAULT_CURRENCY;

  static {
    // from default configuration file (config.properties)
    var currencyString = new ConfigurationProvider()
        .get()
        .getProperty("mts.default.currency");
    DEFAULT_CURRENCY = Currency.getInstance(currencyString);
  }

  public Money(BigDecimal amount) {
    this(DEFAULT_CURRENCY, amount);
  }

  public Money(Currency currency, BigDecimal amount) {
    this.currency = currency;
    this.amount = amount;
  }

  public Money add(Money other) {
    guardAgainstDifferentCurrencies(other);
    return new Money(currency, other.amount.add(amount));
  }

  private void guardAgainstDifferentCurrencies(Money other) {
    Validation.validate(other)
        .assureThat(money -> money.currency == currency,
            "cannot perform operation on money with different currencies");
  }

  public Money subtract(Money other) {
    guardAgainstDifferentCurrencies(other);
    return new Money(currency, other.amount.subtract(amount));
  }

  public Currency getCurrency() {
    return currency;
  }

  public BigDecimal getAmount() {
    return amount;
  }
}
