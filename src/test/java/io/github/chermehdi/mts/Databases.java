package io.github.chermehdi.mts;

import static org.jooq.impl.DSL.count;

import io.github.chermehdi.mts.util.DatabaseConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

/**
 * @author chermehdi
 */
public final class Databases {

  private Databases() {
  }

  public static int getRowCount(String tableName) {
    return context().select(count())
        .from(tableName)
        .fetchOne()
        .component1();
  }

  private static DSLContext context() {
    return DSL.using(new DatabaseConnectionProvider().getConnection());
  }
}
