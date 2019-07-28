package io.github.chermehdi.mts;

import io.github.chermehdi.mts.util.DatabaseConnectionProvider;
import java.io.IOException;
import java.io.InputStream;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.engine.execution.BeforeEachMethodAdapter;
import org.junit.jupiter.engine.extension.ExtensionRegistry;

/**
 * Simple extension that recreates the contents of the database each time a test method is invoked
 *
 * @author chermehdi
 */
public class DatabaseTestExtension implements BeforeEachMethodAdapter {

  @Override
  public void invokeBeforeEachMethod(ExtensionContext extensionContext,
      ExtensionRegistry extensionRegistry) throws Throwable {
    var context = DSL.using(new DatabaseConnectionProvider()
        .getConnection());
    context.execute(getSchemaCreationSQL());
  }

  private String getSchemaCreationSQL() throws IOException {
    InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql");
    return read(is);
  }

  private String read(InputStream is) throws IOException {
    byte[] buff = new byte[1 << 10];
    int read;
    StringBuilder result = new StringBuilder();
    while ((read = is.read(buff)) > 0) {
      result.append(new String(buff, 0, read, "utf8"));
    }
    return result.toString();
  }
}
