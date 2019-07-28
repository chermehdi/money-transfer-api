package io.github.chermehdi.mts.domain;

import io.github.chermehdi.mts.util.validation.Validation;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * @author chermehdi
 */
public class Transaction {

  private Long id;
  private String identifier;
  private BigDecimal amount;
  private Instant performedAt;

  public Transaction() {
  }

  public Transaction(Long id, BigDecimal amount, String identifier) {
    this.id = id;
    this.identifier = Validation.notNull(identifier);
    this.amount = Validation.notNull(amount);
    this.performedAt = Instant.now();
  }

  public Transaction(Long id, BigDecimal amount, Instant performedAt) {
    this(id, amount, UUID.randomUUID().toString());
    this.performedAt = Validation.notNull(performedAt);
  }
}
