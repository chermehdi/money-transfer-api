package io.github.chermehdi.mts.domain.exception;

/**
 * Parent class for all operation wise exceptions
 *
 * @author chermehdi
 */
public class OperationException extends RuntimeException {

  public OperationException(String message) {
    super(message);
  }
}
