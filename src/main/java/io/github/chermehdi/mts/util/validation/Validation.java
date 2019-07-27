package io.github.chermehdi.mts.util.validation;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author chermehdi
 */
public final class Validation {

  public static <T> ValidationSubject<T> validate(T value) {
    return new ValidationSubject<>(value);
  }

  public static <T> T notNull(T value) {
    return new ValidationSubject<>(value)
        .assureThat(v -> v != null)
        .get();
  }

  public static <T> T notNull(T value, String message) {
    return new ValidationSubject<>(value)
        .assureThat(v -> v != null, message)
        .get();
  }

  public static class ValidationSubject<T> {

    private final T value;

    public ValidationSubject(T value) {
      if (value == null) {
        throw new ValidationException("validated value cannot be null");
      }
      this.value = value;

    }

    public ValidationSubject<T> assureThat(Predicate<T> predicate) {
      return assureThat(predicate, "a validation error occurred");
    }

    public ValidationSubject<T> assureThat(Predicate<T> predicate, String message) {
      Objects.requireNonNull(predicate);
      if (!predicate.test(value)) {
        throw new ValidationException(message);
      }
      return this;
    }

    public T get() {
      return value;
    }
  }
}
