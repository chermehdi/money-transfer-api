package io.github.chermehdi.mts.service;

import io.github.chermehdi.mts.domain.Account;
import io.github.chermehdi.mts.repository.AccountRepository;
import io.github.chermehdi.mts.util.validation.ValidationException;
import java.util.List;
import javax.inject.Inject;

/**
 * @author chermehdi
 */
public class AccountService {

  private final AccountRepository accountRepository;

  @Inject
  public AccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public Account getAccount(String identifier) {
    var optionalAccount = accountRepository.findByIdentifier(identifier);
    return optionalAccount.orElseThrow(
        () -> new ValidationException("account with identifier: " + identifier + " not found"));
  }

  public List<Account> getAllAccounts() {
    return accountRepository.findAll();
  }
}
