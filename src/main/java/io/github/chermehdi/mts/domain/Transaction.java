package io.github.chermehdi.mts.domain;

import io.github.chermehdi.mts.util.validation.Validation;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author chermehdi
 */
public class Transaction {

  private String id;
  private BigDecimal amount;
  private Instant performedAt;

  public Transaction() {
  }

  public Transaction(String id, BigDecimal amount) {
    this.id = id;
    this.amount = Validation.notNull(amount);
    this.performedAt = Instant.now();
  }

  public Transaction(String id, BigDecimal amount, Instant performedAt) {
    this(id, amount);
    this.performedAt = Validation.notNull(performedAt);
  }
}
