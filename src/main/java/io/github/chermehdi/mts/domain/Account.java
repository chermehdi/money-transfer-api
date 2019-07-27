package io.github.chermehdi.mts.domain;

import io.github.chermehdi.mts.util.validation.Validation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chermehdi
 */
public class Account {

  private String id;
  private BigDecimal balance;
  private List<Transaction> transactions;

  public Account() {
    balance = BigDecimal.ZERO;
    transactions = new ArrayList<>();
  }

  public Account(String id, BigDecimal initialBalance) {
    this.id = id;
    balance = Validation.validate(initialBalance)
        .assureThat(balance -> balance.compareTo(BigDecimal.ZERO) >= 0,
            "Cannot create an account with negative balance"
        ).get();
    transactions = new ArrayList<>();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }
}
