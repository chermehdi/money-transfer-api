package io.github.chermehdi.mts.controller;

import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.domain.Transfer;
import io.github.chermehdi.mts.domain.User;
import io.github.chermehdi.mts.dto.TransferRequest;
import io.github.chermehdi.mts.dto.UserCreationRequest;
import io.github.chermehdi.mts.service.AccountService;
import io.github.chermehdi.mts.service.TransferService;
import io.github.chermehdi.mts.service.UserService;
import io.github.chermehdi.mts.util.conversion.JsonConverter;
import java.util.List;
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
  public Transfer performTransfer(Request req, Response res) {
    var transferRequest = jsonConverter.parse(req.body(), TransferRequest.class);
    return transferService.performTransfer(transferRequest);
  }

  @NotNull
  public List<Transfer> getAllTransfers(Request req, Response res) {
    return transferService.getAllTransfers();
  }

  @NotNull
  public Account getAccountByIdentifier(Request req, Response res) {
    return accountService.getAccount(req.params(":account_id"));
  }

  @NotNull
  public List<Account> getAllAccounts(Request req, Response res) {
    return accountService.getAllAccounts();
  }

  @NotNull
  public List<User> getAllUsers(Request req, Response res) {
    return userService.getAllUsers();
  }

  @NotNull
  public User createUser(Request req, Response res) {
    UserCreationRequest userCreationRequest = jsonConverter
        .parse(req.body(), UserCreationRequest.class);
    return userService.createUser(userCreationRequest);
  }

}
