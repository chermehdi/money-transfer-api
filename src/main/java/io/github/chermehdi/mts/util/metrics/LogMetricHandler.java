package io.github.chermehdi.mts.util.metrics;

import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

/**
 * This implementation is the default implementation, it only logs some request attributes using a
 * logger.
 *
 * @author chermehdi
 */
public class LogMetricHandler implements MetricHandler {

  private Logger logger;

  @Override
  public CompletableFuture<Void> writeMetrics(Request request) {
    return CompletableFuture.runAsync(
        () -> logger().info(
            "New " + request.requestMethod() + " request: " + request.ip() + " - " + request.host()
                + " --- " + request.pathInfo())
    );
  }

  private Logger logger() {
    return logger == null ? (logger = LoggerFactory.getLogger(LogMetricHandler.class)) : logger;
  }
}
