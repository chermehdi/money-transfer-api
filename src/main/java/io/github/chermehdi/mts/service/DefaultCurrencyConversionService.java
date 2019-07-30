package io.github.chermehdi.mts.service;

import io.github.chermehdi.mts.domain.Money;
import io.github.chermehdi.mts.util.validation.Validation;
import java.util.Currency;

/**
 * This is a default (dummy) implementation of the service, it will return the same amount if the
 * currencies are equal, and it will throw an exception otherwise.
 *
 * @author chermehdi
 */
public class DefaultCurrencyConversionService implements CurrencyConversionService {

  @Override
  public Money convert(Money amount, Currency currency) {
    Validation.notNull(currency, "Currency must be none null");

    Validation.validate(amount)
        .assureThat(money -> money != null, "The given amount must be none null")
        .assureThat(money -> money.getCurrency().equals(currency),
            "cannot convert between two different currencies");

    return amount;
  }
}
