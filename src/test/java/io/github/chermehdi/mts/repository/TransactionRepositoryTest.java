package io.github.chermehdi.mts.repository;

import static io.github.chermehdi.mts.domain.Money.DEFAULT_COMPARISON_SCALE;
import static io.github.chermehdi.mts.domain.tables.Transaction.TRANSACTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.chermehdi.mts.DatabaseTestExtension;
import io.github.chermehdi.mts.Databases;
import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.domain.Money;
import io.github.chermehdi.mts.domain.Transaction;
import io.github.chermehdi.mts.util.DatabaseConnectionProvider;
import io.github.chermehdi.mts.util.validation.ValidationException;
import java.io.IOException;
import java.math.BigDecimal;
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
class TransactionRepositoryTest {

  private DSLContext context;

  @BeforeEach
  void initialize() throws IOException {
    context = DSL.using(new DatabaseConnectionProvider()
        .getConnection());
  }

  @Test
  public void testCanCreate() {
    var transactionRepository = new TransactionRepository(context);

    assertThrows(ValidationException.class, () -> {
      new TransactionRepository(null);
    });
  }

  @Test
  public void testFindById() {
    var transactionRepository = new TransactionRepository(context);
    var existingTransactionId = 1L;
    var optionalTransaction = transactionRepository.findById(existingTransactionId);

    assertTrue(optionalTransaction.isPresent());
    assertEquals(existingTransactionId, optionalTransaction.get().getId());
  }

  @Test
  public void testFindAll() {
    var transactionRepository = new TransactionRepository(context);
    var allTransactions = transactionRepository.findAll();

    assertFalse(allTransactions.isEmpty());
  }

  @Test
  public void testFindAllShouldReturnAll() {
    var transactionRepository = new TransactionRepository(context);
    var allTransactions = transactionRepository.findAll();

    var transactionCount = Databases.getRowCount(TRANSACTION.getName());

    assertEquals(transactionCount, allTransactions.size());
  }

  @Test
  public void testPersistTransaction() {
    var transactionRepository = new TransactionRepository(context);
    var accountRepository = new AccountRepository(context);
    var account = new Account(null, new Money(BigDecimal.valueOf(1)));
    accountRepository.persist(account);

    var transaction = new Transaction(null, new Money(BigDecimal.valueOf(100)));
    transaction = transactionRepository.persist(transaction, account);

    assertNotNull(transaction.getId());
    assertEquals(BigDecimal.valueOf(100).setScale(DEFAULT_COMPARISON_SCALE),
        transaction.getAmount().getAmount().setScale(DEFAULT_COMPARISON_SCALE));
    assertTrue(transactionRepository.findById(transaction.getId()).isPresent());
  }

  @Test
  public void testUpdateTransaction() {
    var transactionRepository = new TransactionRepository(context);
    var existingTransactionId = 1L;
    var transaction = transactionRepository.findById(existingTransactionId).get();
    var newAmount = BigDecimal.valueOf(200L);

    transaction.setAmount(new Money(newAmount));
    transactionRepository.update(transaction);

    assertEquals(newAmount.setScale(DEFAULT_COMPARISON_SCALE),
        transactionRepository.findById(existingTransactionId).get().getAmount().getAmount()
            .setScale(
                DEFAULT_COMPARISON_SCALE));
  }

  @Test
  public void testDeleteTransaction() {
    var transactionRepository = new TransactionRepository(context);
    var existingTransactionId = 1L;
    transactionRepository.delete(existingTransactionId);

    assertFalse(transactionRepository.findById(existingTransactionId).isPresent());
  }
}