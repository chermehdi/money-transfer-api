package io.github.chermehdi.mts.domain.exception;

/**
 * @author chermehdi
 */
public class TransferOperationException extends RuntimeException {

  public TransferOperationException() {
    super("Could not complete the transfer operation");
  }

  public TransferOperationException(String message) {
    super(message);
  }
}
