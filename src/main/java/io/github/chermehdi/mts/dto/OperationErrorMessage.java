package io.github.chermehdi.mts.dto;

import io.github.chermehdi.mts.domain.Transfer.OperationStatus;

/**
 * @author chermehdi
 */
public class OperationErrorMessage {

  private String message;
  private OperationStatus status;

  public OperationErrorMessage() {
  }

  public OperationErrorMessage(String message, OperationStatus operationStatus) {
    this.message = message;
    this.status = operationStatus;
  }

  public OperationErrorMessage(String message) {
    this.message = message;
    this.status = OperationStatus.FAILED;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public OperationStatus getStatus() {
    return status;
  }

  public void setStatus(OperationStatus status) {
    this.status = status;
  }

}
