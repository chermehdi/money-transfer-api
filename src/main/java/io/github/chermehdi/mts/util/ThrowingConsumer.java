package io.github.chermehdi.mts.util;

/**
 * @author chermehdi
 */
public interface ThrowingConsumer<T> {

  void accept(T value) throws Exception;
}
