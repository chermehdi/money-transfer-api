package io.github.chermehdi.mts.repository;

import static io.github.chermehdi.mts.domain.tables.User.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.chermehdi.mts.DatabaseTestExtension;
import io.github.chermehdi.mts.Databases;
import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.domain.FullName;
import io.github.chermehdi.mts.domain.User;
import io.github.chermehdi.mts.util.DatabaseConnectionProvider;
import io.github.chermehdi.mts.util.validation.ValidationException;
import java.io.IOException;
import java.util.Optional;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author chermehdi
 */
@Tag("database-test")
@ExtendWith(DatabaseTestExtension.class)
class UserRepositoryTest {

  private DSLContext context;

  @BeforeEach
  void initialize() throws IOException {
    context = DSL.using(new DatabaseConnectionProvider()
        .getConnection());
  }

  @Test
  public void testCreateUserRepository() {
    var repository = new UserRepository(context, mock(AccountRepository.class));
  }

  @Test
  public void testThrowsIfAnyCreationParameterIsNull() {
    assertThrows(ValidationException.class, () ->
        new UserRepository(null, mock(AccountRepository.class))
    );
    assertThrows(ValidationException.class, () ->
        new UserRepository(context, null)
    );
  }

  @Test
  public void testFindUserById() {
    var mockAccountRepository = mock(AccountRepository.class);
    var repository = new UserRepository(context, mockAccountRepository);
    when(mockAccountRepository.findById(anyInt())).thenReturn(Optional.of(new Account()));
    var optionalUser = repository.findById(1);

    assertTrue(optionalUser.isPresent());
    verify(mockAccountRepository).findById(anyInt());
    assertNotNull(optionalUser.get().getAccount());
  }

  @Test
  public void testPersistUser() {
    var mockAccountRepository = mock(AccountRepository.class);
    var repository = new UserRepository(context, mockAccountRepository);

    var user = new User(null, "user1", "user1");
    var persistedUser = repository.persist(user);

    assertNotNull(persistedUser);
    assertNotNull(persistedUser.getId());
    assertEquals("user1", persistedUser.getFirstName());

    var optionalInsertedUser = repository.findById(persistedUser.getId());
    assertTrue(optionalInsertedUser.isPresent());
    assertEquals("user1", optionalInsertedUser.get().getFirstName());
  }

  @Test
  public void testUpdateUserThrowIfUserNotAlreadyPersisted() {
    var mockAccountRepository = mock(AccountRepository.class);
    var repository = new UserRepository(context, mockAccountRepository);

    assertThrows(ValidationException.class, () -> repository.update(new User()));
  }

  @Test
  public void testUpdateUser() {
    var mockAccountRepository = mock(AccountRepository.class);
    var repository = new UserRepository(context, mockAccountRepository);

    var user = repository.findById(1).get();
    user.setFullName(new FullName("user1", "user1"));
    repository.update(user);
    user = repository.findById(1).get();

    assertEquals("user1", user.getFirstName());
    assertEquals("user1", user.getLastName());
  }

  @Test
  public void testDeleteUserIgnoresNotFoundUsers() {
    var mockAccountRepository = mock(AccountRepository.class);
    var repository = new UserRepository(context, mockAccountRepository);

    var noneExistingUserId = 12323;
    try {
      repository.delete(noneExistingUserId);
    } catch (Exception e) {
      fail("should not throw an exception");
    }
  }

  @Test
  public void testDeleteUser() {
    var mockAccountRepository = mock(AccountRepository.class);
    var repository = new UserRepository(context, mockAccountRepository);
    var existingUserId = 1;
    repository.delete(existingUserId);

    assertFalse(repository.findById(existingUserId).isPresent());
  }

  @Test
  public void testDeleteUserShouldDeleteAccount() {
    var mockAccountRepository = new AccountRepository(context);
    var repository = new UserRepository(context, mockAccountRepository);
    var existingUserId = 1;
    var user = repository.findById(existingUserId).get();

    assertNotNull(user.getAccount());
    assertTrue(repository.findById(user.getAccount().getId()).isPresent());

    repository.delete(existingUserId);
    assertFalse(repository.findById(existingUserId).isPresent());
    assertFalse(repository.findById(user.getAccount().getId()).isPresent());
  }

  @Test
  public void testFindAllShouldReturnTheCorrectCount() {
    var mockAccountRepository = new AccountRepository(context);
    var repository = new UserRepository(context, mockAccountRepository);

    var rowCount = Databases.getRowCount(USER.getName());
    var allUsers = repository.findAll();
    assertNotNull(allUsers);
    assertEquals(rowCount, allUsers.size());
  }
}