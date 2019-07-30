package io.github.chermehdi.mts.service;

import io.github.chermehdi.mts.domain.Money;
import java.util.Currency;

/**
 * Currency conversion service used to transfer money from a given currency to another, the
 * conversion rate can be obtained through some kind of external webservice, or a long running
 * websocket.
 *
 * @author chermehdi
 * @see {@link DefaultCurrencyConversionService} for the default implementation.
 */
public interface CurrencyConversionService {

  Money convert(Money amount, Currency currency);
}
