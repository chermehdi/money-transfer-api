package io.github.chermehdi.mts.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.chermehdi.mts.util.validation.Validation;
import java.time.Instant;

/**
 * @author chermehdi
 */
public class Transfer {

  @JsonIgnore
  private Long id;
  private String fromAccountId;
  private String toAccountId;
  private Money amount;
  private Instant performedAt;
  private OperationStatus status;

  public Transfer() {
  }

  public Transfer(Long id, String fromAccountId, String toAccountId, Money amount,
      OperationStatus status) {
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

  public OperationStatus getStatus() {
    return status;
  }

  public void setStatus(OperationStatus status) {
    this.status = status;
  }

  public enum OperationStatus {
    SUCCESS("Success"),
    FAILED("Failed");

    private String displayName;

    OperationStatus(String displayName) {
      this.displayName = displayName;
    }
  }
}
