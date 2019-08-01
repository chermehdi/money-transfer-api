package io.github.chermehdi.mts;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.chermehdi.mts.config.ApplicationModule;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Extension to boot the DI container, with the spark server to allow web requests to be processed
 * on every test run, and when all tests are finished, server is going to be stoped.
 *
 * @author chermehdi
 */
public class WebTestExtension implements BeforeAllCallback, AfterAllCallback {

  Injector injector = InjectorInstanceHolder.getInjector();

  @Override
  public void beforeAll(ExtensionContext extensionContext) throws Exception {
    var application = injector.getInstance(MoneyTransferApplication.class);
    application.start();
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    var application = injector.getInstance(MoneyTransferApplication.class);
    application.stop();
  }

  /**
   * Cache for the injector, so it can be used in further tests
   */
  public static class InjectorInstanceHolder {

    private static Injector injector;

    public static Injector getInjector() {
      if (injector == null) {
        injector = Guice.createInjector(new ApplicationModule());
      }
      return injector;
    }
  }
}
