package com.accountservice.daos;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.accountservice.entities.Customer;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {
    Optional<Customer> getCustomerByBusinessId(final String bId);

    @Transactional
    void deleteByBusinessId(final String bid);
}
