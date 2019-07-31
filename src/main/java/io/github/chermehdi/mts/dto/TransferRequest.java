package io.github.chermehdi.mts.dto;

import java.math.BigDecimal;

/**
 * @author chermehdi
 */
public class TransferRequest {

  private String fromAccountId;
  private String toAccountId;
  private String currency;
  private BigDecimal amount;

  public TransferRequest() {
  }

  public String getFromAccountId() {
    return fromAccountId;
  }

  public void setFromAccountId(String fromAccountId) {
    this.fromAccountId = fromAccountId;
  }

  public String getToAccountId() {
    return toAccountId;
  }

  public void setToAccountId(String toAccountId) {
    this.toAccountId = toAccountId;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
}
