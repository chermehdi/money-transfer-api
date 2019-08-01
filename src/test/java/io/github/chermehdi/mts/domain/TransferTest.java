package io.github.chermehdi.mts.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.chermehdi.mts.domain.Transfer.OperationStatus;
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
      new Transfer(1L, null, "", new Money(BigDecimal.ONE), OperationStatus.SUCCESS);
    });
    assertThrows(ValidationException.class, () -> {
      new Transfer(1L, "", null, new Money(BigDecimal.ONE), OperationStatus.SUCCESS);
    });
    assertThrows(ValidationException.class, () -> {
      new Transfer(1L, "", "", null, OperationStatus.SUCCESS);
    });
    assertThrows(ValidationException.class, () -> {
      new Transfer(1L, "", "", new Money(BigDecimal.ONE), null);
    });
  }
}