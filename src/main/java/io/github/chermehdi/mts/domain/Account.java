package io.github.chermehdi.mts.domain;

import io.github.chermehdi.mts.util.validation.Validation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author chermehdi
 */
public class Account {

  private Integer id;
  private String identifier;
  private Money balance;
  private List<Transaction> transactions = new ArrayList<>();

  public Account() {
  }

  public Account(Integer id, Money initialBalance) {
    this(id, initialBalance, UUID.randomUUID().toString());
  }


  public Account(Integer id, Money initialBalance, String identifier) {
    this.id = id;
    this.identifier = identifier;
    balance = Validation.validate(initialBalance)
        .assureThat(balance -> balance.getAmount().compareTo(BigDecimal.ZERO) >= 0,
            "Cannot create an account with negative balance"
        ).get();
  }


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
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

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public String toString() {
    return "Account{" +
        "id=" + id +
        ", identifier='" + identifier + '\'' +
        ", balance=" + balance +
        ", transactions=" + transactions +
        '}';
  }
}
