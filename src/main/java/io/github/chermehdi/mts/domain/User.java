package io.github.chermehdi.mts.domain;

import java.util.Objects;

/**
 * @author chermehdi
 */
public class User {

  private Integer id;
  private String firstName;
  private String lastName;
  private Account account;

  public User() {
  }

  public User(Integer id, String firstName, String lastName) {
    this.id = id;
    this.firstName = Objects.requireNonNull(firstName);
    this.lastName = Objects.requireNonNull(lastName);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }
}
