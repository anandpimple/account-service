package com.accountservice.services;

import com.accountservice.entities.Customer;
import com.accountservice.models.CustomerRequest;
import com.accountservice.models.CustomerResponse;

public interface CustomerService extends DataService<CustomerResponse, CustomerRequest, Customer> {
}
