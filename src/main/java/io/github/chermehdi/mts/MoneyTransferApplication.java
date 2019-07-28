package io.github.chermehdi.mts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

/**
 * @author chermehdi
 */
public class MoneyTransferApplication {

  private static final Logger logger = LoggerFactory.getLogger(MoneyTransferApplication.class);

  public void start() {
    bootDependencyInjectionContainer();
    registerGlobalHandlers();
    registerRoutes();
  }

  private void bootDependencyInjectionContainer() {
  }

  private void registerGlobalHandlers() {
    Spark.before("/*", (request, response) ->
        logger.info(
            "New " + request.requestMethod() + " request: " + request.ip() + " - " + request.host()
                + " --- " + request.pathInfo()));
  }

  public void registerRoutes() {
  }

  public static void main(String[] args) {
    new MoneyTransferApplication().start();
  }
}
