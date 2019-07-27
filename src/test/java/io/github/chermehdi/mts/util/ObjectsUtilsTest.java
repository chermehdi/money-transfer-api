package io.github.chermehdi.mts.util;

import static io.github.chermehdi.mts.util.ObjectsUtils.consumeUnchecked;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author chermehdi
 */
class ObjectsUtilsTest {

  @Test
  public void testCanSupplySuppressed() throws Exception {
    ThrowingSupplier<String> mockedSupplier = Mockito.mock(ThrowingSupplier.class);
    when(mockedSupplier.get()).thenReturn("some string");
    Supplier<String> supplier = ObjectsUtils.supplyUnchecked(mockedSupplier);
    assertEquals("some string", supplier.get());
  }

  @Test
  public void testWrapsExceptionInRuntimeException() throws Exception {
    ThrowingSupplier<String> mockedSupplier = Mockito.mock(ThrowingSupplier.class);

    doThrow(new Exception()).when(mockedSupplier).get();

    assertThrows(RuntimeException.class, () -> {
      ObjectsUtils.supplyUnchecked(mockedSupplier)
          .get();
    });
  }

  @Test
  public void testCanConsumeSuppressed() throws Exception {
    ThrowingConsumer<String> consumer = Mockito.mock(ThrowingConsumer.class);
    var suppressedConsumer = (Consumer<String>) consumeUnchecked(consumer);
    final String valueToConsume = "some value";
    suppressedConsumer.accept(valueToConsume);

    verify(consumer).accept(valueToConsume);
  }

  @Test
  public void testConsumeSuppressedThrowsRuntimeException() throws Exception {
    ThrowingConsumer<String> consumer = Mockito.mock(ThrowingConsumer.class);
    doAnswer(invocationOnMock -> {
      throw new Exception();
    }).when(consumer).accept(anyString());

    var suppressedConsumer = (Consumer<String>) consumeUnchecked(consumer);
    final String valueToConsume = "some value";
    assertThrows(RuntimeException.class, () -> {
      suppressedConsumer.accept(valueToConsume);
    });

    verify(consumer).accept(valueToConsume);
  }

  private void exceptionGuard(boolean shouldThrow) throws Exception {
    if (shouldThrow) {
      throw new Exception();
    }
  }
}