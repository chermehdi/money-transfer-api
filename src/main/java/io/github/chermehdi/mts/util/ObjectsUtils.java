package io.github.chermehdi.mts.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author chermehdi
 */
public final class ObjectsUtils {

  private ObjectsUtils() {
    throw new IllegalStateException(
        "Cannot instantiate ObjectsUtils class, use static methods provided");
  }

  public static <T> Supplier<T> supplyUnchecked(ThrowingSupplier<T> supplier) {
    return () -> {
      try {
        return supplier.get();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };
  }

  public static <T> Consumer<T> consumeUnchecked(ThrowingConsumer<T> consumer) {
    return value -> {
      try {
        consumer.accept(value);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    };
  }
}
