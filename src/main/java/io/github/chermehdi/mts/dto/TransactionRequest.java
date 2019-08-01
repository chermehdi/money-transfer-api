package io.github.chermehdi.mts.dto;

import java.math.BigDecimal;

/**
 * @author chermehdi
 */
public class TransactionRequest {

  private BigDecimal amount;
  private String currency;

  public TransactionRequest() {
  }

  public TransactionRequest(BigDecimal amount, String currency) {
    this.amount = amount;
    this.currency = currency;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }
}
