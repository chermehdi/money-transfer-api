package io.github.chermehdi.mts.dto;

import io.github.chermehdi.mts.domain.FullName;
import io.github.chermehdi.mts.domain.User;
import java.math.BigDecimal;

/**
 * @author chermehdi
 */
public class UserResponse {

  private FullName fullName;
  private BigDecimal balance;
  private String preferredCurrency;

  public UserResponse() {
  }

  public UserResponse(FullName fullName, BigDecimal balance, String preferredCurrency) {
    this.fullName = fullName;
    this.balance = balance;
    this.preferredCurrency = preferredCurrency;
  }

  public static UserResponse of(User user) {
    return new UserResponse(
        new FullName(user.getFirstName(), user.getLastName()),
        user.getAccount().getBalance().getAmount(),
        user.getAccount().getBalance().getCurrency().getCurrencyCode()
    );
  }

  public FullName getFullName() {
    return fullName;
  }

  public void setFullName(FullName fullName) {
    this.fullName = fullName;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public String getPreferredCurrency() {
    return preferredCurrency;
  }

  public void setPreferredCurrency(String preferredCurrency) {
    this.preferredCurrency = preferredCurrency;
  }
}
