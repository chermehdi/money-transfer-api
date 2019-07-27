package io.github.chermehdi.mts.util;

/**
 * @author chermehdi
 */
public interface ThrowingSupplier<T> {

  T get() throws Exception;
}
