package io.github.chermehdi.mts.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import io.github.chermehdi.mts.MoneyTransferApplication;
import io.github.chermehdi.mts.service.CurrencyConversionService;
import io.github.chermehdi.mts.service.DefaultCurrencyConversionService;
import io.github.chermehdi.mts.util.ConfigurationProvider;
import io.github.chermehdi.mts.util.ConfigurationProvider.Configuration;
import io.github.chermehdi.mts.util.DatabaseConnectionProvider;
import io.github.chermehdi.mts.util.conversion.JsonResponseTransformer;
import io.github.chermehdi.mts.util.metrics.LogMetricHandler;
import io.github.chermehdi.mts.util.metrics.MetricHandler;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
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
    bind(DSLContext.class)
        .toProvider(() -> DSL.using(new DatabaseConnectionProvider().getConnection()));
    bind(CurrencyConversionService.class).to(DefaultCurrencyConversionService.class);

    bind(MoneyTransferApplication.class).in(Singleton.class);
  }
}
