package io.github.chermehdi.mts.util.validation;

/**
 * @author chermehdi
 */
public class ValidationException extends RuntimeException {

  public ValidationException() {
    super("A validation exception occurred");
  }

  public ValidationException(String message) {
    super(message);
  }
}
