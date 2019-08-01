package io.github.chermehdi.mts.domain;

import io.github.chermehdi.mts.util.validation.Validation;

/**
 * @author chermehdi
 */
public class FullName {

  private String firstName;
  private String lastName;

  // for json serialisation
  public FullName() {
    firstName = lastName = "";
  }

  public FullName(String firstName, String lastName) {
    this.firstName = Validation.notNull(firstName);
    this.lastName = Validation.notNull(lastName);
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }
}
