package io.github.chermehdi.mts.domain;

import io.github.chermehdi.mts.util.validation.Validation;
import java.time.Instant;
import java.util.UUID;

/**
 * @author chermehdi
 */
public class Transaction {

  private Long id;
  private String identifier;
  private Money amount;
  private Instant performedAt;

  public Transaction() {
  }

  public Transaction(Long id, Money amount) {
    this(id, amount, UUID.randomUUID().toString(), Instant.now());
  }

  public Transaction(Long id, Money amount, String identifier) {
    this(id, amount, identifier, Instant.now());
  }

  public Transaction(Long id, Money amount, String identifier, Instant performedAt) {
    this.id = id;
    this.amount = Validation.notNull(amount);
    this.identifier = Validation.notNull(identifier);
    this.performedAt = Validation.notNull(performedAt);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
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
}
