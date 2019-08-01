package io.github.chermehdi.mts.dto;

/**
 * @author chermehdi
 */
public class UserCreationResponse {

  private String accountId;

  public UserCreationResponse() {
  }

  public UserCreationResponse(String accountId) {
    this.accountId = accountId;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }
}
