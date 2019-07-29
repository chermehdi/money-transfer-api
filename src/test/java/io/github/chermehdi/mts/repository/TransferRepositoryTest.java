package io.github.chermehdi.mts.repository;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.chermehdi.mts.DatabaseTestExtension;
import io.github.chermehdi.mts.domain.Money;
import io.github.chermehdi.mts.domain.Transfer;
import io.github.chermehdi.mts.util.DatabaseConnectionProvider;
import io.github.chermehdi.mts.util.validation.ValidationException;
import java.io.IOException;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author chermehdi
 */
@Tag("database-test")
@ExtendWith(DatabaseTestExtension.class)
class TransferRepositoryTest {

  private DSLContext context;

  @BeforeEach
  void initialize() throws IOException {
    context = DSL.using(new DatabaseConnectionProvider()
        .getConnection());
  }

  @Test
  public void testCreateTransferRepository() {
    new TransferRepository(context);
    assertThrows(ValidationException.class, () -> new TransferRepository(null));
  }

  @Test
  public void testFindByIdThrowsIfIdIsNull() {
    var transferRepository = new TransferRepository(context);
    assertThrows(ValidationException.class, () -> transferRepository.findById(null));
  }

  @Test
  public void testFindById() {
    var transferRepository = new TransferRepository(context);
    var existingTransferId = 1L;
    var noneExistingTransferId = 1123L;
    var transfer = transferRepository.findById(existingTransferId);
    var noneExistingTransfer = transferRepository.findById(noneExistingTransferId);

    assertTrue(transfer.isPresent());
    assertNotNull(transfer.get().getId());
    assertTrue(noneExistingTransfer.isEmpty());
  }

  @Test
  public void testPersistShouldThrow() {
    var transferRepository = new TransferRepository(context);
    var transfer = new Transfer(1L, "", "", new Money(valueOf(1L)));

    assertThrows(ValidationException.class, () -> transferRepository.persist(transfer));
  }

  @Test
  public void testPersist() {
    var transferRepository = new TransferRepository(context);
    var transfer = new Transfer(null, "", "", new Money(valueOf(1L)));
    var persistedTransfer = transferRepository.persist(transfer);

    assertNotNull(persistedTransfer);
    assertNotNull(persistedTransfer.getId());
  }

  @Test
  public void testUpdate() {
    var transferRepository = new TransferRepository(context);
    var existingTransferId = 1L;
    var transfer = transferRepository.findById(existingTransferId).get();
    var newFromAccountId = "234-234n234-023234";
    var newToAccountId = "234-234n234-023423";
    transfer.setFromAccountId(newFromAccountId);
    transfer.setToAccountId(newToAccountId);
    transferRepository.update(transfer);

    var updatedTransfer = transferRepository.findById(existingTransferId).get();
    assertEquals(newFromAccountId, updatedTransfer.getFromAccountId());
    assertEquals(newToAccountId, updatedTransfer.getToAccountId());
  }

  @Test
  public void testDelete() {
    var transferRepository = new TransferRepository(context);
    var existingTransferId = 1L;
    var optionalTransfer = transferRepository.findById(existingTransferId);

    assertTrue(optionalTransfer.isPresent());
    transferRepository.delete(optionalTransfer.get().getId());

    var transferAfterDelete = transferRepository.findById(existingTransferId);
    assertTrue(transferAfterDelete.isEmpty());
  }
}
