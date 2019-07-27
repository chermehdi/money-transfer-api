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
  private Money balance;
  private List<Transaction> transactions = new ArrayList<>();

  public Account() {

  }

  public Account(String id, Money initialBalance) {
    this.id = id;
    balance = Validation.validate(initialBalance)
        .assureThat(balance -> balance.getAmount().compareTo(BigDecimal.ZERO) >= 0,
            "Cannot create an account with negative balance"
        ).get();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Money getBalance() {
    return balance;
  }

  public void setBalance(Money balance) {
    this.balance = balance;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }
}
