package io.github.chermehdi.mts.repository;

import static io.github.chermehdi.mts.domain.tables.Transaction.TRANSACTION;

import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.domain.Transaction;
import io.github.chermehdi.mts.domain.tables.records.TransactionRecord;
import io.github.chermehdi.mts.util.validation.Validation;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;

/**
 * @author chermehdi
 */
public class TransactionRepository {

  private final DSLContext context;

  public TransactionRepository(DSLContext context) {
    this.context = Validation.notNull(context);
  }

  public Optional<Transaction> findById(Long key) {
    return Optional.ofNullable(context.selectFrom(TRANSACTION)
        .where(TRANSACTION.ID.eq(key))
        .fetchOne(new TransactionMapper()));
  }

  public List<Transaction> findAll() {
    return context.selectFrom(TRANSACTION)
        .fetch(new TransactionMapper());
  }

  public Transaction persist(Transaction entity, Account account) {
    Validation.validate(account)
        .assureThat(ac -> ac != null, "account should be none null")
        .assureThat(ac -> ac.getId() != null,
            "the account should have an id, i.e should be persisted in the database");

    TransactionRecord transactionRecord = context.insertInto(TRANSACTION)
        .set(TRANSACTION.ACCOUNT_ID, account.getId())
        .set(TRANSACTION.AMOUNT, entity.getAmount())
        .set(TRANSACTION.IDENTIFIER, entity.getIdentifier())
        .set(TRANSACTION.PERFORMEDAT, Timestamp.from(entity.getPerformedAt()))
        .returning(TRANSACTION.ID)
        .fetchOne();

    transactionRecord = Validation.notNull(transactionRecord);
    entity.setId(transactionRecord.getId());
    return entity;
  }

  public void update(Transaction entity) {
    Validation.validate(entity)
        .assureThat(tr -> tr != null, "Transaction should be none null")
        .assureThat(tr -> tr.getId() != null, "Transaction should be persisted to database");

    context.update(TRANSACTION)
        .set(TRANSACTION.AMOUNT, entity.getAmount())
        .set(TRANSACTION.IDENTIFIER, entity.getIdentifier())
        .set(TRANSACTION.PERFORMEDAT, Timestamp.from(entity.getPerformedAt()))
        .execute();
  }

  public void delete(Long key) {
    findById(key).ifPresent(transaction -> {
      context.deleteFrom(TRANSACTION)
          .where(TRANSACTION.ID.eq(key))
          .execute();
    });
  }

  public static class TransactionMapper implements RecordMapper<Record, Transaction> {

    @Override
    public Transaction map(Record record) {
      Long id = record.getValue(TRANSACTION.ID);
      BigDecimal amount = record.getValue(TRANSACTION.AMOUNT);
      String identifier = record.getValue(TRANSACTION.IDENTIFIER);
      Instant performedAt = record.getValue(TRANSACTION.PERFORMEDAT).toInstant();
      var transaction = new Transaction(id, amount, identifier);
      transaction.setPerformedAt(performedAt);
      return transaction;
    }
  }
}
