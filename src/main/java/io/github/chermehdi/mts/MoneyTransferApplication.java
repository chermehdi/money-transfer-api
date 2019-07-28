package io.github.chermehdi.mts;

import static io.github.chermehdi.mts.domain.tables.Account.ACCOUNT;

import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.domain.Money;
import io.github.chermehdi.mts.util.DatabaseConnectionProvider;
import java.math.BigDecimal;
import java.util.Currency;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

/**
 * @author chermehdi
 */
public class MoneyTransferApplication {

  private static final Logger logger = LoggerFactory.getLogger(MoneyTransferApplication.class);

  public void start() {
    Account account = DSL.using(new DatabaseConnectionProvider().getConnection())
        .select()
        .from(ACCOUNT)
        .where(ACCOUNT.ID.eq(1))
        .fetchOne(new RecordMapper<Record, Account>() {
          @Override
          public Account map(Record record) {
            Integer id = record.getValue(ACCOUNT.ID);
            BigDecimal amount = record.getValue(ACCOUNT.AMOUNT);
            String currency = record.getValue(ACCOUNT.CURRENCY);
            String identifier = record.getValue(ACCOUNT.IDENTIFIER);
            return new Account(id, new Money(amount, Currency.getInstance(currency)), identifier);
          }
        });
    logger.info("The account is " + account);
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
