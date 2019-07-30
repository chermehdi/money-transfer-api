package io.github.chermehdi.mts.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import io.github.chermehdi.mts.MoneyTransferApplication;
import io.github.chermehdi.mts.util.ConfigurationProvider;
import io.github.chermehdi.mts.util.ConfigurationProvider.Configuration;
import io.github.chermehdi.mts.util.conversion.JsonResponseTransformer;
import io.github.chermehdi.mts.util.metrics.LogMetricHandler;
import io.github.chermehdi.mts.util.metrics.MetricHandler;
import spark.ResponseTransformer;

/**
 * @author chermehdi
 */
public class ApplicationModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(MetricHandler.class).to(LogMetricHandler.class);
    bind(ResponseTransformer.class).to(JsonResponseTransformer.class);
    bind(Configuration.class)
        .toProvider(() -> new ConfigurationProvider().get())
        .in(Singleton.class);
    bind(MoneyTransferApplication.class).in(Singleton.class);
  }
}
