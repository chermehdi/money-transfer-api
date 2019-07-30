package io.github.chermehdi.mts.util.metrics;

import java.util.concurrent.CompletableFuture;
import spark.Request;

/**
 * This interface can be used to add metric dumping support, an implementation can use the
 * attributes provided by the request to send them to a graphana instance or another service for
 * analyzing.
 *
 * The service is explicitly returning a {@code CompletableFuture} to indicate that whatever
 * processing is done it should be done in an async way to not cause performance problems, or block
 * the request execution.
 *
 * @author chermehdi
 */
public interface MetricHandler {

  CompletableFuture<Void> writeMetrics(Request request);
}
