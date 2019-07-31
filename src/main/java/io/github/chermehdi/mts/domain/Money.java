package io.github.chermehdi.mts.domain;

import io.github.chermehdi.mts.util.ConfigurationProvider;
import io.github.chermehdi.mts.util.validation.Validation;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * @author chermehdi
 */
public class Money {

  public static int DEFAULT_COMPARISON_SCALE = 5;

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
    this(amount, DEFAULT_CURRENCY);
  }

  public Money(BigDecimal amount, Currency currency) {
    this.currency = currency;
    this.amount = amount;
  }

  public Money(BigDecimal amount, String currencyCode) {
    this.currency = Currency.getInstance(currencyCode);
    this.amount = amount;
  }

  public Money add(Money other) {
    guardAgainstDifferentCurrencies(other);
    return new Money(other.amount.add(amount), currency);
  }

  private void guardAgainstDifferentCurrencies(Money other) {
    Validation.validate(other)
        .assureThat(money -> money != null, "amount should be none null")
        .assureThat(money -> money.currency == currency,
            "cannot perform operation on money with different currencies");
  }

  public Money subtract(Money other) {
    guardAgainstDifferentCurrencies(other);
    return new Money(amount.subtract(other.amount), currency);
  }

  public boolean isPositive() {
    return amount.setScale(5).compareTo(BigDecimal.ZERO.setScale(5)) >= 0;
  }

  public boolean isBiggerThan(final Money other) {
    guardAgainstDifferentCurrencies(other);
    return amount.setScale(DEFAULT_COMPARISON_SCALE)
        .compareTo(other.getAmount().setScale(DEFAULT_COMPARISON_SCALE)) > 0;
  }

  public Currency getCurrency() {
    return currency;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  @Override
  public String toString() {
    return "Money{" +
        "currency=" + currency +
        ", amount=" + amount +
        '}';
  }
}
