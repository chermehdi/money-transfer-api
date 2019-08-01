package io.github.chermehdi.mts.dto;

import io.github.chermehdi.mts.domain.Account;
import java.math.BigDecimal;

/**
 * @author chermehdi
 */
public class AccountResponse {

  private String identifier;
  private BigDecimal balance;
  private String currency;

  public AccountResponse() {
  }

  public AccountResponse(Account account) {
    identifier = account.getIdentifier();
    balance = account.getBalance().getAmount();
    currency = account.getBalance().getCurrency().getCurrencyCode();
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}
