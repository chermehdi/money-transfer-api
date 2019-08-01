package io.github.chermehdi.mts.dto;

import io.github.chermehdi.mts.domain.Transfer.OperationStatus;
import java.math.BigDecimal;

/**
 * @author chermehdi
 */
public class TransactionResponse {

  private BigDecimal amount;
  private String currency;
  private OperationStatus operationStatus;

  public TransactionResponse() {
  }

  public TransactionResponse(BigDecimal amount, String currency,
      OperationStatus operationStatus) {
    this.amount = amount;
    this.currency = currency;
    this.operationStatus = operationStatus;
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

  public OperationStatus getOperationStatus() {
    return operationStatus;
  }

  public void setOperationStatus(OperationStatus operationStatus) {
    this.operationStatus = operationStatus;
  }
}
