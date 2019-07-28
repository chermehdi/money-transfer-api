package io.github.chermehdi.mts.util;

import io.github.chermehdi.mts.util.ConfigurationProvider.Configuration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.function.Supplier;

/**
 * @author chermehdi
 */
public class DatabaseConnectionProvider {

  private final Configuration configuration;

  public DatabaseConnectionProvider() {
    configuration = new ConfigurationProvider()
        .addFile("build.properties")
        .get();
  }

  public Connection getConnection() {
    Supplier<Connection> connectionSupplier = ObjectsUtils
        .supplyUnchecked((() -> DriverManager.getConnection(
            configuration.getProperty("db.url"),
            configuration.getProperty("db.username"),
            configuration.getProperty("db.password")
        )));

    return connectionSupplier.get();
  }
}
