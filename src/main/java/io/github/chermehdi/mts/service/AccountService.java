package io.github.chermehdi.mts.service;

import static io.github.chermehdi.mts.domain.tables.Account.ACCOUNT;
import static io.github.chermehdi.mts.domain.tables.Transaction.TRANSACTION;

import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.domain.Money;
import io.github.chermehdi.mts.domain.exception.OperationException;
import io.github.chermehdi.mts.domain.exception.ResourceNotFoundException;
import io.github.chermehdi.mts.dto.TransactionRequest;
import io.github.chermehdi.mts.repository.AccountRepository;
import io.github.chermehdi.mts.repository.AccountRepository.AccountMapper;
import java.util.List;
import java.util.UUID;
import javax.inject.Inject;
import org.jooq.DSLContext;

/**
 * @author chermehdi
 */
public class AccountService {

  private final AccountRepository accountRepository;
  private final CurrencyConversionService conversionService;

  @Inject
  public AccountService(AccountRepository accountRepository,
      CurrencyConversionService conversionService) {
    this.accountRepository = accountRepository;
    this.conversionService = conversionService;
  }

  public Account getAccount(String identifier) {
    var optionalAccount = accountRepository.findByIdentifier(identifier);
    return optionalAccount.orElseThrow(
        () -> new ResourceNotFoundException(
            "account with identifier: " + identifier + " not found"));
  }

  public List<Account> getAllAccounts() {
    return accountRepository.findAll();
  }

  public Account performTransaction(String accountId, TransactionRequest transactionRequest) {
    return accountRepository.retreiveTransactional(context -> {
      // record level locking
      var account = context.selectFrom(ACCOUNT)
          .where(ACCOUNT.IDENTIFIER.eq(accountId))
          .forUpdate()
          .fetchOne(new AccountMapper());
      if (account == null) {
        throw new OperationException("invalid AccountId");
      }
      var moneyToTransfer = new Money(transactionRequest.getAmount(),
          transactionRequest.getCurrency());

      var amountInAccountCurrency = conversionService
          .convert(moneyToTransfer, account.getBalance().getCurrency());

      if (moneyToTransfer.isPositive()) {
        performDepositTransaction(context, account, amountInAccountCurrency);
      } else {
        performWithdrawTransaction(context, account, amountInAccountCurrency);
      }
      account.setBalance(account.getBalance().add(amountInAccountCurrency));
      return account;
    });
  }

  private void performWithdrawTransaction(DSLContext context, Account account,
      Money amountInAccountCurrency) {

    // account_balance + (-amount_to_withdraw)
    if (!account.getBalance().add(amountInAccountCurrency).isPositive()) {
      throw new OperationException("Withdraw amount is bigger than account balance");
    }

    updateAccountBalance(context, account, amountInAccountCurrency);

    createTransaction(context, account, amountInAccountCurrency);
  }


  private void performDepositTransaction(DSLContext context, Account account,
      Money amountInAccountCurrency) {

    updateAccountBalance(context, account, amountInAccountCurrency);

    createTransaction(context, account, amountInAccountCurrency);
  }

  private void createTransaction(DSLContext context, Account account, Money moneyToTransfer) {
    context.insertInto(TRANSACTION)
        .set(TRANSACTION.CURRENCY, moneyToTransfer.getCurrency().getCurrencyCode())
        .set(TRANSACTION.AMOUNT, moneyToTransfer.getAmount())
        .set(TRANSACTION.ACCOUNT_ID, account.getId())
        .set(TRANSACTION.IDENTIFIER, UUID.randomUUID().toString())
        .execute();
  }

  private void updateAccountBalance(DSLContext context, Account account,
      Money amountInAccountCurrency) {
    context.update(ACCOUNT)
        .set(ACCOUNT.AMOUNT, account.getBalance().add(amountInAccountCurrency).getAmount())
        .where(ACCOUNT.ID.eq(account.getId()))
        .execute();
  }
}
