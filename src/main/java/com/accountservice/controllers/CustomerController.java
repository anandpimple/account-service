package com.accountservice.controllers;

import static com.accountservice.Constants.CUSTOMER_BID_REGEX;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.accountservice.models.CustomerRequest;
import com.accountservice.models.CustomerResponse;
import com.accountservice.models.PageResponse;
import com.accountservice.services.CustomerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Validated
@Api(value = "customers", tags = {"customers"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {})
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(final CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping(value = "/customers", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "CreateCustomer", notes = "To create new customer")
    public CustomerResponse createCustomer(@Valid @NotNull @RequestBody final CustomerRequest createCustomerRequest) {
        return customerService.create(createCustomerRequest);
    }

    @GetMapping(value = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "FindAllCustomers", notes = "To get all customers in system")
    public PageResponse<CustomerResponse> getAllCustomers(@ApiParam(required = true, example = "25", defaultValue = "25") @RequestParam(defaultValue = "25") @Max(500) int size,
                                                          @ApiParam(required = true, example = "0", defaultValue = "0") @RequestParam(defaultValue = "0") @Min(0) int pageNo) {
        return customerService.findAll(size, pageNo);
    }

    @GetMapping(value = "/customers/{bId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "GetCustomerByBusinessId", notes = "To get customer by its business id")
    public CustomerResponse getCustomerByBid(@Valid @PathVariable("bId") @Pattern(regexp = CUSTOMER_BID_REGEX) final String bId) {
        return customerService.findByBid(bId);
    }

    @DeleteMapping(value = "/customers/{bId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "DeleteCustomer", notes = "To soft delete customer")
    public void deleteCustomerByBid(@Valid @PathVariable("bId") @Pattern(regexp = CUSTOMER_BID_REGEX) final String bId) {
        customerService.delete(bId);
    }
}
