package io.github.chermehdi.mts.controller;

import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.domain.Money;
import io.github.chermehdi.mts.domain.Transfer.OperationStatus;
import io.github.chermehdi.mts.domain.exception.OperationException;
import io.github.chermehdi.mts.dto.AccountResponse;
import io.github.chermehdi.mts.dto.TransactionRequest;
import io.github.chermehdi.mts.dto.TransactionResponse;
import io.github.chermehdi.mts.dto.TransferRequest;
import io.github.chermehdi.mts.dto.TransferResponse;
import io.github.chermehdi.mts.dto.UserCreationRequest;
import io.github.chermehdi.mts.dto.UserCreationResponse;
import io.github.chermehdi.mts.dto.UserResponse;
import io.github.chermehdi.mts.service.AccountService;
import io.github.chermehdi.mts.service.TransferService;
import io.github.chermehdi.mts.service.UserService;
import io.github.chermehdi.mts.util.conversion.JsonConverter;
import io.github.chermehdi.mts.util.validation.Validation;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import spark.Request;
import spark.Response;

/**
 * @author chermehdi
 */
public class MoneyController {

  private final UserService userService;
  private final AccountService accountService;
  private final TransferService transferService;
  private final JsonConverter jsonConverter;

  @Inject
  public MoneyController(UserService userService,
      AccountService accountService,
      TransferService transferService,
      JsonConverter jsonConverter) {
    this.userService = userService;
    this.accountService = accountService;
    this.transferService = transferService;
    this.jsonConverter = jsonConverter;
  }

  @NotNull
  public TransferResponse performTransfer(Request req, Response res) {
    var transferRequest = jsonConverter.parse(req.body(), TransferRequest.class);
    return new TransferResponse(transferService.performTransfer(transferRequest));
  }

  @NotNull
  public List<TransferResponse> getAllTransfers(Request req, Response res) {
    return transferService.getAllTransfers()
        .stream()
        .map(TransferResponse::new)
        .collect(Collectors.toList());
  }

  @NotNull
  public AccountResponse getAccountByIdentifier(Request req, Response res) {
    return new AccountResponse(accountService.getAccount(req.params(":account_id")));
  }

  @NotNull
  public List<AccountResponse> getAllAccounts(Request req, Response res) {
    return accountService.getAllAccounts()
        .stream()
        .map(AccountResponse::new)
        .collect(Collectors.toList());
  }

  @NotNull
  public List<UserResponse> getAllUsers(Request req, Response res) {
    return userService.getAllUsers()
        .stream()
        .map(UserResponse::of)
        .collect(Collectors.toList());
  }

  @NotNull
  public UserCreationResponse createUser(Request req, Response res) {
    var userCreationRequest = jsonConverter
        .parse(req.body(), UserCreationRequest.class);
    res.status(201); // created
    var createdUser = userService.createUser(userCreationRequest);
    return new UserCreationResponse(createdUser.getAccount().getIdentifier());
  }

  @NotNull
  public TransactionResponse performTransaction(Request request, Response response) {
    var transactionRequest = jsonConverter.parse(request.body(), TransactionRequest.class);
    var accountId = request.params(":account_id");
    Validation.notNull(accountId);
    validateTransactionRequest(transactionRequest);
    Account updatedAccount = accountService.performTransaction(accountId, transactionRequest);
    return new TransactionResponse(updatedAccount.getBalance().getAmount(),
        updatedAccount.getBalance().getCurrency().getCurrencyCode(), OperationStatus.SUCCESS);
  }

  private void validateTransactionRequest(TransactionRequest transactionRequest) {
    Validation.validate(transactionRequest)
        .assureThat(tr -> tr != null, "Transaction request must be none null")
        .assureThat(tr -> tr.getAmount() != null, "Transaction amount must be none null")
        .assureThat(tr -> tr.getCurrency() != null, "Transaction currency must be specified")
        .assureThat(tr -> !new Money(tr.getAmount(), tr.getCurrency()).isZero(),
            new OperationException("Transaction amount should be different than 0"));
  }
}
