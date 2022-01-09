package com.accountservice.services;

import java.sql.Timestamp;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;

import com.accountservice.daos.CustomerRepository;
import com.accountservice.entities.Customer;
import com.accountservice.models.CustomerRequest;
import com.accountservice.models.CustomerResponse;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public PagingAndSortingRepository<Customer, Long> getRepository() {
        return customerRepository;
    }

    @Override
    public CustomerResponse mapResponseFromEntity(final Customer customer) {
        return CustomerResponse.builder()
            .withCustomerId(customer.getBusinessId())
            .withFirstName(customer.getFirstName())
            .withLastName(customer.getLastName())
            .withCreatedOn(customer.getCreatedOn().toLocalDateTime())
            .withModifiedOn(Optional.ofNullable(customer.getModifiedOn()).map(Timestamp::toLocalDateTime).orElse(null))
            .build();
    }

    @Override
    public Customer mapEntityFromRequest(final CustomerRequest customerRequest) {
        Customer customer = new Customer();
        customer.setFirstName(customerRequest.getFirstName());
        customer.setLastName(customerRequest.getLastName());
        return customer;
    }

    @Override
    public String getName() {
        return "Customer";
    }

    @Override
    public Optional<Customer> getByBusinessId(final String bId) {
        return customerRepository.getCustomerByBusinessId(bId);
    }

}
