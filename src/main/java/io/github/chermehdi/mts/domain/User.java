package io.github.chermehdi.mts.domain;

import io.github.chermehdi.mts.util.validation.Validation;

/**
 * @author chermehdi
 */
public class User {

  private Integer id;
  private Account account;
  private FullName fullName;

  public User() {
  }

  public User(Integer id, String firstName, String lastName) {
    this.id = id;
    this.fullName = new FullName(firstName, lastName);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getFirstName() {
    return fullName.getFirstName();
  }

  public String getLastName() {
    return fullName.getLastName();
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public void setFullName(FullName fullName) {
    Validation.notNull(fullName);
    this.fullName = fullName;
  }
}
