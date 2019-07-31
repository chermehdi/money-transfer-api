package io.github.chermehdi.mts;

import static spark.Spark.get;
import static spark.Spark.post;

import com.google.inject.Guice;
import io.github.chermehdi.mts.config.ApplicationModule;
import io.github.chermehdi.mts.controller.MoneyController;
import io.github.chermehdi.mts.dto.ErrorMessageResponse;
import io.github.chermehdi.mts.util.ConfigurationProvider.Configuration;
import io.github.chermehdi.mts.util.conversion.JsonConverter;
import io.github.chermehdi.mts.util.metrics.MetricHandler;
import io.github.chermehdi.mts.util.validation.ValidationException;
import javax.inject.Inject;
import spark.Filter;
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
  private final MoneyController moneyController;

  @Inject
  public MoneyTransferApplication(
      Configuration configuration, ResponseTransformer responseTransformer,
      MetricHandler metricHandler,
      JsonConverter jsonConverter,
      MoneyController moneyController) {
    this.configuration = configuration;
    this.responseTransformer = responseTransformer;
    this.metricHandler = metricHandler;
    this.moneyController = moneyController;
    this.jsonConverter = jsonConverter;
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
    post("/users", moneyController::createUser, responseTransformer);
    get("/users", moneyController::getAllUsers, responseTransformer);
    get("/accounts", moneyController::getAllAccounts, responseTransformer);
    get("/accounts/:account_id", moneyController::getAccountByIdentifier, responseTransformer);
    get("/transfers", moneyController::getAllTransfers, responseTransformer);
    post("/transfers", moneyController::performTransfer, responseTransformer);
  }

  public static void main(String[] args) {
    var injector = Guice.createInjector(new ApplicationModule());
    var application = injector.getInstance(MoneyTransferApplication.class);
    application.start();
  }
}
