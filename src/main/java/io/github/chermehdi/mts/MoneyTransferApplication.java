package io.github.chermehdi.mts;

import static spark.Spark.get;
import static spark.Spark.post;

import com.google.inject.Guice;
import io.github.chermehdi.mts.config.ApplicationModule;
import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.domain.Transfer;
import io.github.chermehdi.mts.domain.User;
import io.github.chermehdi.mts.dto.ErrorMessageResponse;
import io.github.chermehdi.mts.dto.TransferRequest;
import io.github.chermehdi.mts.dto.UserCreationRequest;
import io.github.chermehdi.mts.service.AccountService;
import io.github.chermehdi.mts.service.TransferService;
import io.github.chermehdi.mts.service.UserService;
import io.github.chermehdi.mts.util.ConfigurationProvider.Configuration;
import io.github.chermehdi.mts.util.conversion.JsonConverter;
import io.github.chermehdi.mts.util.metrics.MetricHandler;
import io.github.chermehdi.mts.util.validation.ValidationException;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;

/**
 * @author chermehdi
 */
public class MoneyTransferApplication {

  private final Configuration configuration;
  private final ResponseTransformer responseTransformer;
  private final MetricHandler metricHandler;
  private final JsonConverter jsonConverter;
  private final UserService userService;
  private final AccountService accountService;
  private final TransferService transferService;

  @Inject
  public MoneyTransferApplication(
      Configuration configuration, ResponseTransformer responseTransformer,
      MetricHandler metricHandler,
      JsonConverter jsonConverter, UserService userService,
      AccountService accountService,
      TransferService transferService) {
    this.configuration = configuration;
    this.responseTransformer = responseTransformer;
    this.metricHandler = metricHandler;
    this.jsonConverter = jsonConverter;
    this.userService = userService;
    this.accountService = accountService;
    this.transferService = transferService;
  }

  public void start() {
    registerGlobalHandlers();
    registerRoutes();
  }

  private void registerGlobalHandlers() {
    Spark.port(Integer.parseInt(configuration.getProperty("mts.web.port")));

    Spark.before("/*", (request, response) ->
        metricHandler.writeMetrics(request)
    );

    Spark.after((Filter) (request, response)
        -> response.header("Content-type", "application/json"));

    Spark.exception(ValidationException.class, (exception, request, response) -> {
      response.status(400); // bad request
      response.header("Content-type", "application/json");
      response.body(jsonConverter.convert(new ErrorMessageResponse(exception.getMessage())));
    });
  }

  public void registerRoutes() {
    post("/users", this::createUser, responseTransformer);
    get("/users", this::getAllUsers, responseTransformer);
    get("/accounts", this::getAllAccounts, responseTransformer);
    get("/accounts/:account_id", this::getAccountByIdentifier, responseTransformer);
    get("/transfers", this::getAllTransfers, responseTransformer);
    post("/transfers", this::performTransfer, responseTransformer);
  }

  @NotNull
  private Transfer performTransfer(Request req, Response res) {
    var transferRequest = jsonConverter.parse(req.body(), TransferRequest.class);
    return transferService.performTransfer(transferRequest);
  }

  @NotNull
  private List<Transfer> getAllTransfers(Request req, Response res) {
    return transferService.getAllTransfers();
  }

  @NotNull
  private Account getAccountByIdentifier(Request req, Response res) {
    return accountService.getAccount(req.params(":account_id"));
  }

  @NotNull
  private List<Account> getAllAccounts(Request req, Response res) {
    return accountService.getAllAccounts();
  }

  @NotNull
  private List<User> getAllUsers(Request req, Response res) {
    return userService.getAllUsers();
  }

  @NotNull
  private User createUser(Request req, Response res) {
    UserCreationRequest userCreationRequest = jsonConverter
        .parse(req.body(), UserCreationRequest.class);
    return userService.createUser(userCreationRequest);
  }

  public static void main(String[] args) {
    var injector = Guice.createInjector(new ApplicationModule());
    var application = injector.getInstance(MoneyTransferApplication.class);
    application.start();
  }
}
