package io.github.chermehdi.mts;

import static io.github.chermehdi.mts.domain.Money.DEFAULT_COMPARISON_SCALE;
import static io.github.chermehdi.mts.domain.tables.User.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.chermehdi.mts.WebTestExtension.InjectorInstanceHolder;
import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.domain.Money;
import io.github.chermehdi.mts.domain.Transaction;
import io.github.chermehdi.mts.domain.Transfer.OperationStatus;
import io.github.chermehdi.mts.domain.User;
import io.github.chermehdi.mts.dto.AccountResponse;
import io.github.chermehdi.mts.dto.OperationErrorMessage;
import io.github.chermehdi.mts.dto.TransactionRequest;
import io.github.chermehdi.mts.dto.TransactionResponse;
import io.github.chermehdi.mts.dto.TransferRequest;
import io.github.chermehdi.mts.dto.TransferResponse;
import io.github.chermehdi.mts.dto.UserCreationRequest;
import io.github.chermehdi.mts.dto.UserCreationResponse;
import io.github.chermehdi.mts.dto.UserResponse;
import io.github.chermehdi.mts.repository.AccountRepository;
import io.github.chermehdi.mts.repository.UserRepository;
import io.github.chermehdi.mts.util.ConfigurationProvider;
import io.github.chermehdi.mts.util.conversion.JsonConverter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author chermehdi
 */
@Tag("api-tests")
@ExtendWith({WebTestExtension.class, DatabaseTestExtension.class})
public class TransferApiTest {

  static String baseUrl;
  static JsonConverter converter;
  static UserRepository userRepository;
  static AccountRepository accountRepository;

  @BeforeAll
  static void init() {
    baseUrl = String.format("http://localhost:%s",
        new ConfigurationProvider().get()
            .getProperty("mts.web.port"));

    var injector = InjectorInstanceHolder.getInjector();
    converter = injector.getInstance(JsonConverter.class);
    userRepository = injector.getInstance(UserRepository.class);
    accountRepository = injector.getInstance(AccountRepository.class);
  }

  @Test
  @DisplayName("Test GET request to /users")
  public void testGetAllUsers() throws IOException, InterruptedException {
    var responseBody = client().send(getRequest("users"), BodyHandlers.ofString())
        .body();

    var users = converter.parse(responseBody, UserResponse[].class);

    assertNotNull(users);

    List<User> allUsers = userRepository.findAll();
    assertEquals(allUsers.size(), users.length);

    for (int i = 0; i < users.length; ++i) {
      var curUser = allUsers.get(i);
      assertEquals(curUser.getFirstName(), users[i].getFullName().getFirstName());
      assertEquals(curUser.getLastName(), users[i].getFullName().getLastName());
      assertEquals(curUser.getAccount().getBalance().getCurrency().getCurrencyCode(),
          users[i].getPreferredCurrency());
      assertEquals(curUser.getAccount().getBalance().getAmount().setScale(
          DEFAULT_COMPARISON_SCALE), users[i].getBalance().setScale(DEFAULT_COMPARISON_SCALE));
    }
  }


  @Test
  @DisplayName("Test POST request to /users")
  public void testCreateNewUser() throws IOException, InterruptedException {
    var creationRequest = new UserCreationRequest("user-1", "user-2", "EUR");
    var allUsers = userRepository.findAll();

    var response = client()
        .send(postRequest(url("users"), creationRequest), BodyHandlers.ofString());

    assertEquals(201, response.statusCode());

    var creationResponse = converter.parse(response.body(), UserCreationResponse.class);
    assertNotNull(creationResponse);
    assertNotNull(creationResponse.getAccountId());

    Optional<Account> optionalAccount = accountRepository
        .findByIdentifier(creationResponse.getAccountId());

    assertTrue(optionalAccount.isPresent());
    assertEquals(allUsers.size() + 1, Databases.getRowCount(USER.getName()));
  }

  @Test
  @DisplayName("Test POST request to /users/:account_id/transactions to deposit")
  public void testDepositOperation() throws IOException, InterruptedException {
    var addedAmount = BigDecimal.valueOf(100);
    var tr = new TransactionRequest(addedAmount, "EUR");
    var account = accountRepository.findByIdEager(1).get();
    var response = client()
        .send(postRequest(url("accounts", account.getIdentifier(), "transactions"), tr),
            BodyHandlers.ofString());
    assertEquals(200, response.statusCode());

    var resultResponse = converter.parse(response.body(), TransactionResponse.class);
    assertEquals(
        addedAmount.add(account.getBalance().getAmount()).setScale(DEFAULT_COMPARISON_SCALE),
        resultResponse.getAmount().setScale(DEFAULT_COMPARISON_SCALE));

    var updatedAccount = accountRepository.findByIdEager(account.getId()).get();

    assertEquals(account.getTransactions().size() + 1, updatedAccount.getTransactions().size());
  }

  @Test
  @DisplayName("Test POST request to /users/:account_id/transactions to withdraw")
  public void testWithDrawOperation() throws IOException, InterruptedException {
    var withdrawAmount = BigDecimal.valueOf(-20);
    var tr = new TransactionRequest(withdrawAmount, "EUR");
    var account = accountRepository.findByIdEager(1).get();
    var response = client()
        .send(postRequest(url("accounts", account.getIdentifier(), "transactions"), tr),
            BodyHandlers.ofString());

    assertEquals(200, response.statusCode());

    var resultResponse = converter.parse(response.body(), TransactionResponse.class);
    assertEquals(
        withdrawAmount.add(account.getBalance().getAmount()).setScale(DEFAULT_COMPARISON_SCALE),
        resultResponse.getAmount().setScale(DEFAULT_COMPARISON_SCALE));

    var updatedAccount = accountRepository.findByIdEager(account.getId()).get();

    assertEquals(account.getTransactions().size() + 1, updatedAccount.getTransactions().size());
  }

  @Test
  @DisplayName("Test POST request to /users/:account_id/transactions to withdraw a bigger than balance amount")
  public void testFailedWithdrawOperationShouldThrow() throws Exception {
    var withdrawAmount = BigDecimal.valueOf(-2000);
    testInvalidAmount(withdrawAmount);
  }

  @Test
  @DisplayName("Test POST request to /users/:account_id/transactions to deposit a 0 amount")
  public void testFailedDepositOperaitonShouldThrow() throws Exception {
    var deposit = BigDecimal.ZERO;
    testInvalidAmount(deposit);
  }

  @Test
  @DisplayName("Test POST request to /transfers to transfer amount between two accounts")
  public void testMakeTransfer() throws IOException, InterruptedException {
    var fromAccount = accountRepository.findByIdEager(1).get();
    var toAccount = accountRepository.findByIdEager(2).get();
    var amount = BigDecimal.valueOf(30);
    var transferCurrency = "EUR";
    var transferRequest = new TransferRequest(fromAccount.getIdentifier(),
        toAccount.getIdentifier(), amount, transferCurrency);

    var response = client()
        .send(postRequest(url("transfers"), transferRequest), BodyHandlers.ofString());

    assertEquals(200, response.statusCode());

    var transferResponse = converter.parse(response.body(), TransferResponse.class);

    assertEquals(transferResponse.getAmount().setScale(DEFAULT_COMPARISON_SCALE),
        amount.setScale(DEFAULT_COMPARISON_SCALE));
    assertEquals(transferResponse.getCurrency(), transferCurrency);

    var fromAccountAfterUpdate = accountRepository.findByIdEager(1).get();
    var toAccountAfterUpdate = accountRepository.findByIdEager(2).get();

    // the transfer was made in the same currency, probably it's good to add a test
    // with transfer from different currencies and see if the {@code CurrencyConversionService}
    // is invoked correctly, but for simplicity reasons i skipped that here
    assertEquals(
        fromAccount.getBalance().getAmount().subtract(amount).setScale(DEFAULT_COMPARISON_SCALE),
        fromAccountAfterUpdate.getBalance().getAmount().setScale(DEFAULT_COMPARISON_SCALE)
    );
    assertEquals(
        toAccount.getBalance().getAmount().add(amount).setScale(DEFAULT_COMPARISON_SCALE),
        toAccountAfterUpdate.getBalance().getAmount().setScale(DEFAULT_COMPARISON_SCALE)
    );

    List<Transaction> fromAccountTransactionsBeforeUpdate = fromAccount.getTransactions();
    List<Transaction> toAccountTransactionsBeforeUpdate = toAccount.getTransactions();

    assertEquals(fromAccountTransactionsBeforeUpdate.size() + 1,
        fromAccountAfterUpdate.getTransactions().size());
    assertEquals(toAccountTransactionsBeforeUpdate.size() + 1,
        toAccountAfterUpdate.getTransactions().size());

    testTransactionsConsistency(fromAccountAfterUpdate);
    testTransactionsConsistency(toAccountAfterUpdate);
  }

  @Test
  @DisplayName("Test POST request to /transfers transfer bigger than balance")
  public void testMakeTransferMoreThanBalanceThrows() throws IOException, InterruptedException {
    var fromAccount = accountRepository.findByIdEager(1).get();
    var toAccount = accountRepository.findByIdEager(2).get();
    var amount = fromAccount.getBalance().add(new Money(BigDecimal.valueOf(100))).getAmount();
    var transferCurrency = "EUR";
    var transferRequest = new TransferRequest(fromAccount.getIdentifier(),
        toAccount.getIdentifier(), amount, transferCurrency);

    var response = client()
        .send(postRequest(url("transfers"), transferRequest), BodyHandlers.ofString());

    assertEquals(400, response.statusCode());

    var errorMessage = converter.parse(response.body(), OperationErrorMessage.class);
    assertEquals("Transferred amount is bigger than the account balance",
        errorMessage.getMessage());
  }

  @Test
  @DisplayName("Test POST request to /transfers negative transfer ")
  public void testMakeNegativeAmountTransferThrows() throws IOException, InterruptedException {
    var fromAccount = accountRepository.findByIdEager(1).get();
    var toAccount = accountRepository.findByIdEager(2).get();
    var amount = BigDecimal.valueOf(-100);
    var transferCurrency = "EUR";
    var transferRequest = new TransferRequest(fromAccount.getIdentifier(),
        toAccount.getIdentifier(), amount, transferCurrency);

    var response = client()
        .send(postRequest(url("transfers"), transferRequest), BodyHandlers.ofString());

    assertEquals(400, response.statusCode());
  }

  @Test
  @DisplayName("Test GET request to /transfers to list all transfers")
  public void testViewAllTransfers() throws IOException, InterruptedException {
    var response = client()
        .send(getRequest("transfers"), BodyHandlers.ofString());

    assertEquals(200, response.statusCode());

    var transfers = converter.parse(response.body(), TransferResponse[].class);
    for (TransferResponse res : transfers) {
      assertNotNull(res.getAmount());
      assertNotNull(res.getFromAccountId());
      assertNotNull(res.getToAccountId());
      assertNotNull(res.getCurrency());
    }
  }


  @Test
  @DisplayName("Test GET request to /accounts/:account_id to find an account by it's identifier")
  public void testFindingAnAccountByIdentifier() throws IOException, InterruptedException {
    var account = accountRepository.findByIdEager(1).get();
    var response = client()
        .send(getRequest("accounts", account.getIdentifier()), BodyHandlers.ofString());

    assertEquals(200, response.statusCode());
    var foundAccount = converter.parse(response.body(), AccountResponse.class);

    assertEquals(account.getIdentifier(), foundAccount.getIdentifier());
    assertEquals(
        account.getBalance().getAmount().setScale(DEFAULT_COMPARISON_SCALE),
        foundAccount.getBalance().setScale(DEFAULT_COMPARISON_SCALE)
    );
    assertEquals(account.getBalance().getCurrency().getCurrencyCode(), foundAccount.getCurrency());
  }

  @Test
  @DisplayName("Test GET request to /accounts/:account_id to try to find an account with unknown identifier")
  public void testFindAccountWithUnknownIdentifierThrows()
      throws IOException, InterruptedException {
    var unknownIdentifier = "ABC-DEF-GED-TEST";

    var response = client()
        .send(getRequest("accounts", unknownIdentifier), BodyHandlers.ofString());

    assertEquals(404, response.statusCode());
  }

  @Test
  @DisplayName("Test GET request to /accounts to list all available accounts")
  public void testGetAllAccounts() throws IOException, InterruptedException {
    var response = client()
        .send(getRequest("accounts"), BodyHandlers.ofString());

    assertEquals(200, response.statusCode());

    var accounts = converter.parse(response.body(), AccountResponse[].class);
    var databaseAccounts = accountRepository.findAll();

    assertEquals(databaseAccounts.size(), accounts.length);
  }

  private void testTransactionsConsistency(Account account) {
    var balance = account.getBalance();
    var transactionSum = account.getTransactions()
        .stream()
        .map(ac -> ac.getAmount())
        .reduce(new Money(BigDecimal.ZERO, account.getBalance().getCurrency()),
            (acc, curValue) -> acc.add(curValue));

    assertEquals(balance.getAmount().setScale(DEFAULT_COMPARISON_SCALE),
        transactionSum.getAmount().setScale(DEFAULT_COMPARISON_SCALE));
  }

  private void testInvalidAmount(BigDecimal amount) throws Exception {
    var tr = new TransactionRequest(amount, "EUR");
    var account = accountRepository.findByIdEager(1).get();
    var response = client()
        .send(postRequest(url("accounts", account.getIdentifier(), "transactions"), tr),
            BodyHandlers.ofString());

    assertEquals(400, response.statusCode());

    var errorMessage = converter.parse(response.body(), OperationErrorMessage.class);
    assertEquals(OperationStatus.FAILED, errorMessage.getStatus());

    var noneUpdatedAccount = accountRepository.findByIdEager(1).get();
    assertEquals(account.getTransactions().size(), noneUpdatedAccount.getTransactions().size());
  }

  private static HttpClient client() {
    return HttpClient.newHttpClient();
  }

  private static HttpRequest getRequest(String... urlParts) {
    return HttpRequest.newBuilder()
        .header("Accept", "application/json")
        .uri(URI.create(url(urlParts)))
        .build();
  }

  private static HttpRequest postRequest(String url, Object object) {
    return HttpRequest.newBuilder()
        .uri(URI.create(url))
        .POST(BodyPublishers.ofString(converter.convert(object)))
        .build();
  }

  private static String url(String... parts) {
    return baseUrl + "/" + Arrays.stream(parts)
        .collect(Collectors.joining("/"));
  }
}
