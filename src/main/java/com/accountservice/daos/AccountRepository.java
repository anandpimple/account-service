package com.accountservice.daos;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.accountservice.entities.Account;
import com.accountservice.entities.Customer;

public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {
    Page<Account> findAccountsByCustomer(final Customer customer, Pageable pageable);

    Optional<Account> getAccountByBusinessId(final String bId);
}
