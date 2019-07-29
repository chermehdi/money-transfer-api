package io.github.chermehdi.mts.repository;

import static io.github.chermehdi.mts.domain.tables.User.USER;

import io.github.chermehdi.mts.domain.User;
import io.github.chermehdi.mts.util.validation.Validation;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;

/**
 * @author chermehdi
 */
public class UserRepository {

  private final DSLContext context;
  private final AccountRepository accountRepository;

  public UserRepository(DSLContext context,
      AccountRepository accountRepository) {
    this.context = Validation.notNull(context);
    this.accountRepository = Validation.notNull(accountRepository);
  }

  public Optional<User> findById(Integer key) {
    Validation.notNull(key);
    return Optional.ofNullable(context.selectFrom(USER)
        .where(USER.ID.eq(key))
        .fetchOne(new UserMapper(accountRepository)));
  }

  public List<User> findAll() {
    return context.selectFrom(USER)
        .fetch(new UserMapper(accountRepository));
  }

  public User persist(User entity) {
    var insertStep = context.insertInto(USER)
        .set(USER.LAST_NAME, entity.getLastName())
        .set(USER.FIRST_NAME, entity.getFirstName());
    if (entity.getAccount() != null && entity.getAccount().getId() != null) {
      insertStep.set(USER.ACCOUNT_ID, entity.getAccount().getId());
    }
    var userRecord = insertStep.returning(USER.ID)
        .fetchOne();
    userRecord = Validation.notNull(userRecord);
    entity.setId(userRecord.getId());
    return entity;
  }

  public void update(User entity) {
    Validation.validate(entity)
        .assureThat(user -> user != null, "User should be none null")
        .assureThat(user -> user.getId() != null,
            "User instance should already be persisted in database");
    var updateStep = context.update(USER)
        .set(USER.LAST_NAME, entity.getLastName())
        .set(USER.FIRST_NAME, entity.getFirstName());

    if (entity.getAccount() != null && entity.getAccount().getId() != null) {
      updateStep.set(USER.ACCOUNT_ID, entity.getAccount().getId());
    }
    updateStep.execute();
  }

  public void delete(Integer key) {
    findById(key).ifPresent(user -> {
      context.deleteFrom(USER)
          .where(USER.ID.eq(key))
          .execute();
    });
  }

  public static class UserMapper implements RecordMapper<Record, User> {

    private final AccountRepository accountRepository;

    public UserMapper(AccountRepository accountRepository) {
      this.accountRepository = accountRepository;
    }

    @Override
    public User map(Record record) {
      var id = record.getValue(USER.ID);
      var firstName = record.getValue(USER.FIRST_NAME);
      var lastName = record.getValue(USER.LAST_NAME);
      var user = new User(id, firstName, lastName);
      var account = accountRepository.findById(record.getValue(USER.ACCOUNT_ID));
      if (account.isPresent()) {
        user.setAccount(account.get());
      }
      return user;
    }
  }
}
