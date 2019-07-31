package io.github.chermehdi.mts.service;

import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.domain.Money;
import io.github.chermehdi.mts.domain.User;
import io.github.chermehdi.mts.dto.UserCreationRequest;
import io.github.chermehdi.mts.repository.AccountRepository;
import io.github.chermehdi.mts.repository.UserRepository;
import io.github.chermehdi.mts.util.validation.Validation;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import javax.inject.Inject;

/**
 * @author chermehdi
 */
public class UserService {

  private final UserRepository userRepository;
  private final AccountRepository accountRepository;

  @Inject
  public UserService(UserRepository userRepository,
      AccountRepository accountRepository) {
    this.userRepository = userRepository;
    this.accountRepository = accountRepository;
  }

  public User createUser(final UserCreationRequest userRequest) {
    validateRequest(userRequest);
    var account = new Account(null, new Money(BigDecimal.ZERO, userRequest.getPreferredCurrency()));
    account = accountRepository.persist(account);
    var user = new User(null, userRequest.getFirstName(), userRequest.getLastName());
    user.setAccount(account);
    return userRepository.persist(user);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  private void validateRequest(UserCreationRequest userRequest) {
    Validation.validate(userRequest)
        .assureThat(req -> req != null)
        .assureThat(req -> req.getFirstName() != null, "First name should be none null")
        .assureThat(req -> req.getLastName() != null, "Last name should be none null")
        .assureThat(req -> Currency.getAvailableCurrencies()
                .stream()
                .map(Currency::getCurrencyCode)
                .anyMatch(cur -> cur.equals(req.getPreferredCurrency())),
            "Currency must be a valid currency id");
  }
}
