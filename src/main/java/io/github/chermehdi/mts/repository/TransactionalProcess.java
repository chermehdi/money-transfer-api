package io.github.chermehdi.mts.repository;

import io.github.chermehdi.mts.domain.exception.OperationException;
import io.github.chermehdi.mts.util.validation.Validation;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.TransactionalRunnable;
import org.jooq.impl.DSL;

/**
 * @author chermehdi
 */
public abstract class TransactionalProcess {

  public void computeTransactional(Consumer<DSLContext> contextConsumer) {
    context().transaction(configuration -> {
      try {
        var context = DSL.using(configuration);
        contextConsumer.accept(context);
        // automatic commit will be issued here
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  public <T> T retreiveTransactional(Function<DSLContext, T> contextFunction) {
    TransferRepository.StatefullTransactionalRunnable<T> transactionalRunnable = new TransferRepository.StatefullTransactionalRunnable<>(
        contextFunction);
    context().transaction(transactionalRunnable);
    return transactionalRunnable.getValue();
  }


  public static class StatefullTransactionalRunnable<T> implements TransactionalRunnable {

    private T value;
    private Function<DSLContext, T> contextFunction;

    public StatefullTransactionalRunnable(Function<DSLContext, T> contextFunction) {
      this.contextFunction = Validation.notNull(contextFunction);
    }


    public void run(Configuration configuration) throws Throwable {
      try {
        var context = DSL.using(configuration);
        this.value = contextFunction.apply(context);
        // automatic commit will be issued here
      } catch (Exception e) {
        throw new OperationException(e.getMessage());
      }
    }

    public T getValue() {
      return value;
    }
  }

  protected abstract DSLContext context();
}
