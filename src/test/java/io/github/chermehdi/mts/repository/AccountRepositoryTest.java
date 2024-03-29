package io.github.chermehdi.mts.repository;

import static io.github.chermehdi.mts.domain.tables.Account.ACCOUNT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.chermehdi.mts.DatabaseTestExtension;
import io.github.chermehdi.mts.Databases;
import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.domain.Money;
import io.github.chermehdi.mts.util.DatabaseConnectionProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
class AccountRepositoryTest {

  private DSLContext context;

  @BeforeEach
  void initialize() throws IOException {
    context = DSL.using(new DatabaseConnectionProvider()
        .getConnection());
  }

  @Test
  public void testCreateAccountRepository() {
    var repository = new AccountRepository(context);
  }

  @Test
  public void testFindOneAccount() {
    var repository = new AccountRepository(context);
    Optional<Account> optionalAccount = repository.findById(1);
    assertTrue(optionalAccount.isPresent());
  }


  @Test
  public void testEmptyOptionalForNoneExistingAccounts() {
    var repository = new AccountRepository(context);
    var noneExistingAccountId = 123;
    Optional<Account> optionalAccount = repository.findById(noneExistingAccountId);

    assertTrue(optionalAccount.isEmpty());
  }

  @Test
  public void testFindAllAccounts() {
    var repository = new AccountRepository(context);
    List<Account> allAccounts = repository.findAll();
    assertNotNull(allAccounts);
    assertFalse(allAccounts.isEmpty());
    int actualCount = Databases.getRowCount(ACCOUNT.getName());

    assertEquals(allAccounts.size(), actualCount);
  }

  @Test
  public void testDoesNotFindTransactions() {
    var repository = new AccountRepository(context);
    var account = repository.findById(1).get();
    assertTrue(account.getTransactions().isEmpty());
  }

  @Test
  public void testUpdateAccountRecord() {
    var repository = new AccountRepository(context);
    var account = repository.findById(1).get();
    var oldBalance = account.getBalance();
    var newBalance = oldBalance.add(new Money(oldBalance.getAmount(), oldBalance.getCurrency()));
    account.setBalance(newBalance);
    repository.update(account);
    var account1 = repository.findById(1).get();
    assertEquals(newBalance.getAmount(), account1.getBalance().getAmount());
  }

  @Test
  public void testPersistAccount() {
    var repository = new AccountRepository(context);
    Account persistedAccount = repository
        .persist(new Account(null, new Money(BigDecimal.valueOf(100))));
    assertNotNull(persistedAccount);
    assertNotNull(persistedAccount.getId());
    assertEquals(BigDecimal.valueOf(100), persistedAccount.getBalance().getAmount());
    assertTrue(repository.findById(persistedAccount.getId()).isPresent());
  }

  @Test
  public void testDeleteAccountRecord() {
    var repository = new AccountRepository(context);
    repository.delete(1);
    var optionalAccount = repository.findById(1);
    assertTrue(optionalAccount.isEmpty());
  }

  @Test
  public void testFindByIdEagerly() {
    var repository = new AccountRepository(context);
    var lazyAccount = repository.findById(1);
    assertFalse(lazyAccount.isEmpty());
    assertEquals(1, lazyAccount.get().getId());
    assertTrue(lazyAccount.get().getTransactions().isEmpty());

    var eagerAccount = repository.findByIdEager(1);
    var expectedNumberOfTransactions = 2;
    assertFalse(eagerAccount.isEmpty());
    assertEquals(1, eagerAccount.get().getId());
    assertEquals(expectedNumberOfTransactions, eagerAccount.get().getTransactions().size());
  }

  @Test
  public void testFindByIdentifier() {
    var repository = new AccountRepository(context);
    var randomAccountIdentifier = "adf3234-234ljdf-wer";
    var existingAccountIdentifier = "1f442fda-b0c4-40a0-b2f8-89dca5e0b2d8";

    assertFalse(repository.findByIdentifier(randomAccountIdentifier).isPresent());

    assertTrue(repository.findByIdentifier(existingAccountIdentifier).isPresent());
  }
}