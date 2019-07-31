package io.github.chermehdi.mts.service;

import static io.github.chermehdi.mts.domain.tables.Account.ACCOUNT;
import static io.github.chermehdi.mts.domain.tables.Transfer.TRANSFER;

import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.domain.Money;
import io.github.chermehdi.mts.domain.Transfer;
import io.github.chermehdi.mts.domain.Transfer.TransferStatus;
import io.github.chermehdi.mts.domain.exception.TransferOperationException;
import io.github.chermehdi.mts.dto.TransferRequest;
import io.github.chermehdi.mts.repository.AccountRepository.AccountMapper;
import io.github.chermehdi.mts.repository.TransferRepository;
import io.github.chermehdi.mts.util.validation.Validation;
import java.util.List;
import javax.inject.Inject;
import org.jooq.DSLContext;

/**
 * @author chermehdi
 */
public class TransferService {

  private final TransferRepository transferRepository;
  private final CurrencyConversionService conversionService;

  @Inject
  public TransferService(TransferRepository transferRepository,
      CurrencyConversionService conversionService) {
    this.transferRepository = transferRepository;
    this.conversionService = conversionService;
  }

  public List<Transfer> getAllTransfers() {
    return transferRepository.findAll();
  }

  public Transfer performTransfer(TransferRequest transferRequest) {
    validateRequest(transferRequest);
    return transferRepository
        .retreiveTransitionally(context -> makeTransfer(context, transferRequest));
  }

  private Transfer makeTransfer(DSLContext context, TransferRequest request) {
    validateTransferRequest(request);

    var fromAccount = retreiveAccountByIdentifier(context, request.getFromAccountId());
    var toAccount = retreiveAccountByIdentifier(context, request.getToAccountId());

    Validation.notNull(fromAccount);
    Validation.notNull(toAccount);

    var transferAmountInFromAccountCurrency = conversionService
        .convert(new Money(request.getAmount(), request.getCurrency()),
            fromAccount.getBalance().getCurrency());

    guardAgainstNegativeAmountTransfer(transferAmountInFromAccountCurrency);
    guardExceedingBalanceTransfer(fromAccount, transferAmountInFromAccountCurrency);

    updateAccountBalance(context, fromAccount,
        fromAccount.getBalance().subtract(transferAmountInFromAccountCurrency));

    var transferAmountInToAccountCurrency = conversionService
        .convert(transferAmountInFromAccountCurrency,
            toAccount.getBalance().getCurrency());

    updateAccountBalance(context, toAccount,
        toAccount.getBalance().add(transferAmountInToAccountCurrency));

    var transfer = new Transfer(null, fromAccount.getIdentifier(), toAccount.getIdentifier(),
        new Money(request.getAmount(), request.getCurrency()), TransferStatus.SUCCESS);

    // cannot use the {@code TransferRepository}, need to use the context given as parameter
    return saveTransfer(context, transfer);
  }

  private void validateTransferRequest(TransferRequest request) {
    Validation.validate(request)
        .assureThat(req -> req != null, "The transfer request should be none null")
        .assureThat(req -> req.getFromAccountId() != null, "The fromAccountId should be none null")
        .assureThat(req -> req.getToAccountId() != null, "The toAccountId should be none null")
        .assureThat(req -> req.getFromAccountId().equals(req.getToAccountId()),
            "A transfer cannot be made to the same account")
        .assureThat(req -> !new Money(req.getAmount(), req.getCurrency()).isZero(),
            "A transfer with an amount of 0.0 cannot be made");
  }

  private void guardExceedingBalanceTransfer(Account fromAccount,
      Money transferAmountInFromAccountCurrency) {
    if (transferAmountInFromAccountCurrency.isBiggerThan(fromAccount.getBalance())) {
      throw new TransferOperationException("Transferred amount bigger than account balance");
    }
  }

  private void guardAgainstNegativeAmountTransfer(Money transferAmountInFromAccountCurrency) {
    if (!transferAmountInFromAccountCurrency.isPositive()) {
      throw new TransferOperationException("Transferred amount should be positive");
    }
  }

  // it needed to be done, because i needed the request to be done on the given context, and not on
  // the one given to the {@code TransferRepository}
  @SuppressWarnings("Duplicates")
  private Transfer saveTransfer(DSLContext context, Transfer transfer) {
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

    transfer.setId(transferRecord.getId());
    return transfer;
  }

  private void updateAccountBalance(DSLContext context, Account fromAccount, Money newBalance) {
    context.update(ACCOUNT)
        .set(ACCOUNT.AMOUNT, newBalance.getAmount())
        .where(ACCOUNT.ID.eq(fromAccount.getId()))
        .execute();
  }

  private Account retreiveAccountByIdentifier(DSLContext context,
      String identifier) {
    return context.selectFrom(ACCOUNT)
        .where(ACCOUNT.IDENTIFIER.eq(identifier))
        .fetchOne(new AccountMapper());
  }

  private void validateRequest(TransferRequest transferRequest) {
    Validation.validate(transferRequest)
        .assureThat(tr -> tr != null, "Transfer must be none null")
        .assureThat(tr -> tr.getFromAccountId() != null, "Transfer fromAccountId must be none null")
        .assureThat(tr -> tr.getToAccountId() != null, "Transfer toAccountId must be none null")
        .assureThat(tr -> new Money(tr.getAmount(), tr.getCurrency()).isPositive(),
            "transfer amount must be positive");
  }
}
