package io.github.chermehdi.mts.domain.exception;

/**
 * @author chermehdi
 */
public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException() {
    super("Could not find the resource you are looking for");
  }
}
