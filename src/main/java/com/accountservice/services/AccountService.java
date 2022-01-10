package com.accountservice.services;

import com.accountservice.entities.Account;
import com.accountservice.models.AccountRequest;
import com.accountservice.models.AccountResponse;
import com.accountservice.models.PageResponse;

public interface AccountService extends DataService<AccountResponse, AccountRequest, Account> {
    PageResponse<AccountResponse> getAccountsForACustomer(final String customerBid, int pageNo, int size);
}
