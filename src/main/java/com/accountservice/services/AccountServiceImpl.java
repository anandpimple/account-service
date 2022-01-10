package com.accountservice.services;

import static java.util.Optional.ofNullable;

import java.sql.Timestamp;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import com.accountservice.daos.AccountRepository;
import com.accountservice.entities.Account;
import com.accountservice.entities.Customer;
import com.accountservice.exceptions.DataNotFoundException;
import com.accountservice.models.AccountRequest;
import com.accountservice.models.AccountResponse;
import com.accountservice.models.PageResponse;

@Service
public class AccountServiceImpl implements AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountRepository accountRepository;
    private final CustomerService customerService;

    public AccountServiceImpl(final AccountRepository accountRepository,
                              final CustomerService customerService) {
        this.accountRepository = accountRepository;
        this.customerService = customerService;
    }

    @Override
    public PageResponse<AccountResponse> getAccountsForACustomer(final String customer,
                                                                 int pageNo,
                                                                 int size) {
        logger.info("Finding all accounts for customer with bid {}", customer);
        return mapToPageResponse(accountRepository.findAccountsByCustomer(getCustomer(customer), Pageable.ofSize(size).withPage(pageNo)));
    }


    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public PagingAndSortingRepository<Account, Long> getRepository() {
        return accountRepository;
    }

    @Override
    public AccountResponse mapResponseFromEntity(final Account entity) {
        return AccountResponse
            .builder()
            .withCustomerId(entity.getCustomer().getBusinessId())
            .withCreatedOn(entity.getCreatedOn().toLocalDateTime())
            .withModifiedOn(ofNullable(entity.getModifiedOn()).map(Timestamp::toLocalDateTime).orElse(null))
            .withCurrency(entity.getCurrency())
            .withAccountId(entity.getBusinessId())
            .withDescription(entity.getDescription())
            .withName(entity.getName())
            .withNumber(entity.getNumber())
            .withSortCode(entity.getSortCode())
            .build();
    }

    @Override
    public Account mapEntityFromRequest(final AccountRequest accountRequest) {
        Account account = new Account();
        account.setCurrency(accountRequest.getCurrency());
        account.setCustomer(getCustomer(accountRequest.getCustomerBid()));
        account.setDescription(accountRequest.getDescription());
        account.setName(accountRequest.getName());
        account.setNumber(accountRequest.getNumber());
        account.setSortCode(accountRequest.getSortCode());
        return account;
    }

    private Customer getCustomer(final String bid) {
        return customerService
            .getByBusinessId(bid)
            .orElseThrow(() -> new DataNotFoundException(String.format("Customer not found for bid '%s'", bid)));
    }

    @Override
    public String getName() {
        return "Account";
    }

    @Override
    public Optional<Account> getByBusinessId(String bId) {
        return accountRepository.getAccountByBusinessId(bId);
    }
}
