package io.github.chermehdi.mts.util.validation;

import java.util.Objects;
import java.util.function.Function;
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
      return assureThat(predicate, "a validation error occurred",
          message -> new ValidationException(message));
    }

    public ValidationSubject<T> assureThat(Predicate<T> predicate, String message) {
      return assureThat(predicate, message, msg -> new ValidationException(msg));
    }

    public ValidationSubject<T> assureThat(Predicate<T> predicate, RuntimeException ex) {
      return assureThat(predicate, "a validation error occurred", message -> ex);
    }

    public ValidationSubject<T> assureThat(Predicate<T> predicate, String message,
        Function<String, RuntimeException> exceptionProducer) {
      Objects.requireNonNull(predicate);
      if (!predicate.test(value)) {
        throw exceptionProducer.apply(message);
      }
      return this;
    }

    public T get() {
      return value;
    }
  }
}
