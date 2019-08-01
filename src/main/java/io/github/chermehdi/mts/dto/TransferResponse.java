package io.github.chermehdi.mts.dto;

import io.github.chermehdi.mts.domain.Transfer;
import io.github.chermehdi.mts.domain.Transfer.OperationStatus;
import java.math.BigDecimal;

/**
 * @author chermehdi
 */
public class TransferResponse {

  private OperationStatus status;
  private BigDecimal amount;
  private String currency;
  private String fromAccountId;
  private String toAccountId;

  public TransferResponse() {
  }

  public TransferResponse(OperationStatus status, BigDecimal amount, String currency) {
    this.status = status;
    this.amount = amount;
    this.currency = currency;
  }

  public TransferResponse(Transfer transfer) {
    status = transfer.getStatus();
    amount = transfer.getAmount().getAmount();
    currency = transfer.getAmount().getCurrency().getCurrencyCode();
    fromAccountId = transfer.getFromAccountId();
    toAccountId = transfer.getToAccountId();
  }

  public OperationStatus getStatus() {
    return status;
  }

  public void setStatus(OperationStatus status) {
    this.status = status;
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
}
