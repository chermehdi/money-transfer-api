package io.github.chermehdi.mts.dto;

/**
 * @author chermehdi
 */
public class UserCreationRequest {

  private String firstName;
  private String lastName;
  private String preferredCurrency;

  public UserCreationRequest() {
  }

  public UserCreationRequest(String firstName, String lastName, String preferredCurrency) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.preferredCurrency = preferredCurrency;
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

  public String getPreferredCurrency() {
    return preferredCurrency;
  }

  public void setPreferredCurrency(String preferredCurrency) {
    this.preferredCurrency = preferredCurrency;
  }
}
