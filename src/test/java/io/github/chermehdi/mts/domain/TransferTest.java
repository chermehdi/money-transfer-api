package io.github.chermehdi.mts.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.chermehdi.mts.util.validation.ValidationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/**
 * @author chermehdi
 */
class TransferTest {

  @Test
  public void testCannotCreateTransferWithAnyNullParameter() {
    assertThrows(ValidationException.class, () -> {
      new Transfer(1L, null, "", new Money(BigDecimal.ONE));
    });
    assertThrows(ValidationException.class, () -> {
      new Transfer(1L, "", null, new Money(BigDecimal.ONE));
    });
    assertThrows(ValidationException.class, () -> {
      new Transfer(1L, "", "", null);
    });
  }
}