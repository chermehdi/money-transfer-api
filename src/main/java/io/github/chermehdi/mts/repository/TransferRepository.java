package io.github.chermehdi.mts.repository;

import static io.github.chermehdi.mts.domain.tables.Transfer.TRANSFER;

import io.github.chermehdi.mts.domain.Money;
import io.github.chermehdi.mts.domain.Transfer;
import io.github.chermehdi.mts.domain.Transfer.TransferStatus;
import io.github.chermehdi.mts.util.validation.Validation;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.inject.Inject;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.TransactionalRunnable;
import org.jooq.impl.DSL;

/**
 * @author chermehdi
 */
public class TransferRepository extends TransactionalProcess {

  private final DSLContext context;

  @Inject
  public TransferRepository(DSLContext context) {
    this.context = Validation.notNull(context);
  }

  public Optional<Transfer> findById(Long id) {
    Validation.notNull(id);
    return Optional.ofNullable(context.selectFrom(TRANSFER)
        .where(TRANSFER.ID.eq(id))
        .fetchOne(new TransferMapper()));
  }

  public List<Transfer> findAll() {
    return context.selectFrom(TRANSFER)
        .fetch(new TransferMapper());
  }

  public Transfer persist(Transfer transfer) {
    Validation.validate(transfer)
        .assureThat(tr -> tr != null)
        .assureThat(tr -> tr.getId() == null, "Transfer should not be persisted in the databse");

    var transferRecord = context.insertInto(TRANSFER)
        .set(TRANSFER.TO_ACCOUNT_IDENTIFIER, transfer.getToAccountId())
        .set(TRANSFER.FROM_ACCOUNT_IDENTIFIER, transfer.getFromAccountId())
        .set(TRANSFER.AMOUNT, transfer.getAmount().getAmount())
        .set(TRANSFER.CURRENCY, transfer.getAmount().getCurrency().getCurrencyCode())
        .set(TRANSFER.STATUS, transfer.getStatus().name())
        .returning(TRANSFER.ID)
        .fetchOne();

    Validation.notNull(transferRecord);
    transfer.setId(transferRecord.getId());

    return transfer;
  }

  public void update(Transfer transfer) {
    Validation.validate(transfer)
        .assureThat(tr -> tr != null)
        .assureThat(tr -> tr.getId() != null,
            "Transfer should not be already persisted in the database to be updated");

    context.update(TRANSFER)
        .set(TRANSFER.TO_ACCOUNT_IDENTIFIER, transfer.getToAccountId())
        .set(TRANSFER.FROM_ACCOUNT_IDENTIFIER, transfer.getFromAccountId())
        .set(TRANSFER.AMOUNT, transfer.getAmount().getAmount())
        .set(TRANSFER.CURRENCY, transfer.getAmount().getCurrency().getCurrencyCode())
        .set(TRANSFER.STATUS, transfer.getStatus().name())
        .execute();

  }

  public void delete(Long id) {
    findById(id).ifPresent(transfer -> {
      context.deleteFrom(TRANSFER)
          .where(TRANSFER.ID.eq(id))
          .execute();
    });
  }

  @Override
  public void computeTransitionally(Consumer<DSLContext> contextConsumer) {
    context.transaction(configuration -> {
      try {
        var context = DSL.using(configuration);
        contextConsumer.accept(context);
        // automatic commit will be issued here
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public <T> T retreiveTransitionally(Function<DSLContext, T> contextFunction) {
    StatefullTransactionalRunnable<T> transactionalRunnable = new StatefullTransactionalRunnable<>(
        contextFunction);
    context.transaction(transactionalRunnable);
    return transactionalRunnable.getValue();
  }


  public static class StatefullTransactionalRunnable<T> implements TransactionalRunnable {

    private T value;
    private Function<DSLContext, T> contextFunction;

    public StatefullTransactionalRunnable(Function<DSLContext, T> contextFunction) {
      this.contextFunction = Validation.notNull(contextFunction);
    }

    @Override
    public void run(Configuration configuration) throws Throwable {
      try {
        var context = DSL.using(configuration);
        this.value = contextFunction.apply(context);
        // automatic commit will be issued here
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    public T getValue() {
      return value;
    }
  }


  public static class TransferMapper implements RecordMapper<Record, Transfer> {

    @Override
    public Transfer map(Record record) {
      return new Transfer(
          record.getValue(TRANSFER.ID),
          record.getValue(TRANSFER.FROM_ACCOUNT_IDENTIFIER),
          record.getValue(TRANSFER.TO_ACCOUNT_IDENTIFIER),
          new Money(record.getValue(TRANSFER.AMOUNT),
              Currency.getInstance(record.getValue(TRANSFER.CURRENCY))),
          TransferStatus.valueOf(record.getValue(TRANSFER.STATUS))
      );
    }
  }
}
