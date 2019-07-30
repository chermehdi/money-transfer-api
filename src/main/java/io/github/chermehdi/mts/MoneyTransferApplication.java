package io.github.chermehdi.mts;

import static spark.Spark.post;

import com.google.inject.Guice;
import io.github.chermehdi.mts.config.ApplicationModule;
import io.github.chermehdi.mts.dto.UserCreationRequest;
import io.github.chermehdi.mts.util.ConfigurationProvider.Configuration;
import io.github.chermehdi.mts.util.conversion.JsonResponseTransformer;
import io.github.chermehdi.mts.util.metrics.MetricHandler;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import spark.Spark;

/**
 * @author chermehdi
 */
public class MoneyTransferApplication {

  private static final Logger logger = LoggerFactory.getLogger(MoneyTransferApplication.class);

  private final Configuration configuration;
  private final ResponseTransformer responseTransformer;
  private final MetricHandler metricHandler;

  @Inject
  public MoneyTransferApplication(
      Configuration configuration, ResponseTransformer responseTransformer,
      MetricHandler metricHandler) {
    this.configuration = configuration;
    this.responseTransformer = responseTransformer;
    this.metricHandler = metricHandler;
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
  }

  public void registerRoutes() {
    post("/users", this::userCreation, new JsonResponseTransformer());
  }

  @NotNull
  private UserCreationRequest userCreation(Request req, Response res) {
    return new UserCreationRequest("mehdi", "cheracher", "EUR");
  }

  public static void main(String[] args) {
    var injector = Guice.createInjector(new ApplicationModule());
    var application = injector.getInstance(MoneyTransferApplication.class);
    application.start();
  }
}
