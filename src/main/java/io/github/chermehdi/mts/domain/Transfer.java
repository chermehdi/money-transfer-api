package io.github.chermehdi.mts.domain;

import io.github.chermehdi.mts.util.validation.Validation;
import java.time.Instant;

/**
 * @author chermehdi
 */
public class Transfer {

  private Long id;
  private String fromAccountId;
  private String toAccountId;
  private Money amount;
  private Instant performedAt;
  private TransferStatus status;

  public Transfer() {
  }

  public Transfer(Long id, String fromAccountId, String toAccountId, Money amount,
      TransferStatus status) {
    this.id = id;
    this.fromAccountId = Validation.notNull(fromAccountId);
    this.toAccountId = Validation.notNull(toAccountId);
    this.amount = Validation.notNull(amount);
    this.performedAt = Instant.now();
    this.status = Validation.notNull(status);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Money getAmount() {
    return amount;
  }

  public void setAmount(Money amount) {
    this.amount = amount;
  }

  public Instant getPerformedAt() {
    return performedAt;
  }

  public void setPerformedAt(Instant performedAt) {
    this.performedAt = performedAt;
  }

  public TransferStatus getStatus() {
    return status;
  }

  public void setStatus(TransferStatus status) {
    this.status = status;
  }

  public enum TransferStatus {
    SUCCESS("Success"),
    FAILED("Failed");

    private String displayName;

    TransferStatus(String displayName) {
      this.displayName = displayName;
    }
  }
}
