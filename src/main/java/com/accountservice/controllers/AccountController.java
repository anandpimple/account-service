package com.accountservice.controllers;

import static com.accountservice.Constants.ACCOUNT_BID_REGEX;
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

import com.accountservice.models.AccountRequest;
import com.accountservice.models.AccountResponse;
import com.accountservice.models.PageResponse;
import com.accountservice.services.AccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Validated
@Api(value = "accounts", tags = {"accounts"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, authorizations = {})
public class AccountController {
    private final AccountService accountService;

    public AccountController(final AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(value = "/accounts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "CreateAccount", notes = "To create new account")
    public AccountResponse createAccount(@Valid @NotNull @RequestBody final AccountRequest accountRequest) {
        return accountService.create(accountRequest);
    }

    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "FindAllAccounts", notes = "To get all accounts in system paginated by size and pageNo ")
    public PageResponse<AccountResponse> findAllAccounts(@ApiParam(required = true, example = "25", defaultValue = "25") @RequestParam(defaultValue = "25") @Max(500) int size,
                                                         @ApiParam(required = true, example = "0", defaultValue = "0") @RequestParam(defaultValue = "0") @Min(0) int pageNo) {
        return accountService.findAll(size, pageNo);
    }

    @GetMapping(value = "/accounts/for/customer/{bId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "FindAllAccountsByCustomer", notes = "To get all accounts for a customer in system paginated by size and pageNo ")
    public PageResponse<AccountResponse> findAllAccountsForCustomer(@Valid @PathVariable("bId") @Pattern(regexp = CUSTOMER_BID_REGEX) final String bid,
                                                                    @ApiParam(required = true, example = "25", defaultValue = "25") @RequestParam(defaultValue = "25") @Max(500) int size,
                                                                    @ApiParam(required = true, example = "0", defaultValue = "0") @RequestParam(defaultValue = "0") @Min(0) int pageNo) {
        return accountService.getAccountsForACustomer(bid, size, pageNo);
    }

    @GetMapping(value = "/accounts/{bId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "GetAccountByBid", notes = "To get account by its business id")
    public AccountResponse getAccountByBid(@Valid @PathVariable("bId") @Pattern(regexp = ACCOUNT_BID_REGEX) final String bId) {
        return accountService.findByBid(bId);
    }

    @DeleteMapping(value = "/accounts/{bId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "DeleteAccount", notes = "To soft delete account by bid")
    public void deleteAccountByBid(@Valid @PathVariable("bId") @Pattern(regexp = CUSTOMER_BID_REGEX) final String bId) {
        accountService.delete(bId);
    }
}