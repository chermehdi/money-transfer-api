package io.github.chermehdi.mts.repository;

import static io.github.chermehdi.mts.domain.tables.Account.ACCOUNT;
import static io.github.chermehdi.mts.domain.tables.Transaction.TRANSACTION;

import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.domain.Money;
import io.github.chermehdi.mts.domain.Transaction;
import io.github.chermehdi.mts.domain.tables.records.AccountRecord;
import io.github.chermehdi.mts.repository.TransactionRepository.TransactionMapper;
import io.github.chermehdi.mts.util.validation.Validation;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;

/**
 * @author chermehdi
 */
public class AccountRepository {

  private final DSLContext context;

  @Inject
  public AccountRepository(DSLContext context) {
    this.context = Validation.notNull(context);
  }

  public Optional<Account> findById(Integer key) {
    var account = context.select()
        .from(ACCOUNT)
        .where(ACCOUNT.ID.eq(key))
        .fetchOne(new AccountMapper());
    return Optional.ofNullable(account);
  }

  public Optional<Account> findByIdentifier(String identifier) {
    var account = context.selectFrom(ACCOUNT)
        .where(ACCOUNT.IDENTIFIER.eq(identifier))
        .fetchOne(new AccountMapper());
    return Optional.ofNullable(account);
  }


  public List<Account> findAll() {
    return context.selectFrom(ACCOUNT)
        .fetch(new AccountMapper());
  }

  public Account persist(Account entity) {
    AccountRecord accountRecord = context.insertInto(ACCOUNT)
        .set(ACCOUNT.IDENTIFIER, entity.getIdentifier())
        .set(ACCOUNT.CURRENCY, entity.getBalance().getCurrency().getCurrencyCode())
        .set(ACCOUNT.AMOUNT, entity.getBalance().getAmount())
        .returning(ACCOUNT.ID)
        .fetchOne();

    accountRecord = Validation.notNull(accountRecord);

    entity.setId(accountRecord.getId());
    return entity;
  }

  public void update(Account entity) {
    Validation.validate(entity)
        .assureThat(ac -> ac != null, "Account should be none null")
        .assureThat(ac -> ac.getId() != null,
            "Account instance should already be persisted in database");

    // transaction list should be handled by the {@code TransactionRepository}
    context.update(ACCOUNT)
        .set(ACCOUNT.AMOUNT, entity.getBalance().getAmount())
        .set(ACCOUNT.CURRENCY, entity.getBalance().getCurrency().getCurrencyCode())
        .set(ACCOUNT.IDENTIFIER, entity.getIdentifier())
        .where(ACCOUNT.ID.eq(entity.getId()))
        .execute();
  }

  public Optional<Account> findByIdEager(Integer id) {
    Optional<Account> optionalAccount = findById(id);
    if (optionalAccount.isPresent()) {
      Account account = optionalAccount.get();
      List<Transaction> transactions = context.selectFrom(TRANSACTION)
          .where(TRANSACTION.ACCOUNT_ID.eq(id))
          .fetch(new TransactionMapper());
      account.setTransactions(transactions);
      return Optional.of(account);
    }
    return optionalAccount;
  }


  public void delete(Integer key) {
    findById(key).ifPresent(account -> {
      context.deleteFrom(ACCOUNT)
          .where(ACCOUNT.ID.eq(key))
          .execute();
    });

  }

  public static class AccountMapper implements RecordMapper<Record, Account> {

    @Override
    public Account map(Record record) {
      var id = record.getValue(ACCOUNT.ID);
      var amount = record.getValue(ACCOUNT.AMOUNT);
      var currency = record.getValue(ACCOUNT.CURRENCY);
      var identifier = record.getValue(ACCOUNT.IDENTIFIER);
      return new Account(id, new Money(amount, currency), identifier);
    }
  }
}
