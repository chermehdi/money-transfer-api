package io.github.chermehdi.mts.repository;

import java.util.function.Consumer;
import java.util.function.Function;
import org.jooq.DSLContext;

/**
 * @author chermehdi
 */
public abstract class TransactionalProcess {

  public abstract void computeTransitionally(Consumer<DSLContext> contextConsumer);

  public abstract <T> T retreiveTransitionally(Function<DSLContext, T> contextConsumer);
}
