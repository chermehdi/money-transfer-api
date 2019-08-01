package io.github.chermehdi.mts.repository;

import static io.github.chermehdi.mts.domain.tables.Transfer.TRANSFER;

import io.github.chermehdi.mts.domain.Money;
import io.github.chermehdi.mts.domain.Transfer;
import io.github.chermehdi.mts.domain.Transfer.OperationStatus;
import io.github.chermehdi.mts.util.validation.Validation;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;

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
  protected DSLContext context() {
    return context;
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
          OperationStatus.valueOf(record.getValue(TRANSFER.STATUS))
      );
    }
  }
}
