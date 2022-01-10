package com.accountservice.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.accountservice.daos.AccountRepository;
import com.accountservice.entities.Account;
import com.accountservice.entities.Customer;
import com.accountservice.exceptions.DataNotFoundException;
import com.accountservice.models.AccountRequest;
import com.accountservice.models.AccountResponse;
import com.accountservice.models.PageResponse;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    AccountRepository accountRepository;
    @Mock
    private CustomerService customerService;
    @Mock
    private Page page;

    @Captor
    private ArgumentCaptor<Account> accountArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> bidArgumentCaptor;

    @InjectMocks
    private AccountServiceImpl underTest;

    private static Account account(final String name,
                                   final String sortCode,
                                   final Integer number,
                                   final String bid) {
        Account account = new Account();
        account.setName(name);
        account.setSortCode(sortCode);
        account.setNumber(number);
        account.setBusinessId(bid);
        account.setCreatedOn(Timestamp.valueOf(LocalDateTime.now()));
        Customer customer = new Customer();
        customer.setBusinessId("cbid");
        account.setCustomer(customer);
        return account;
    }

    private static AccountResponse accountResponse(final Account account) {
        return AccountResponse
            .builder()
            .withSortCode(account.getSortCode())
            .withAccountId(account.getBusinessId())
            .withNumber(account.getNumber())
            .withName(account.getName())
            .withCustomerId(account.getCustomer().getBusinessId())
            .withCreatedOn(account.getCreatedOn().toLocalDateTime())
            .build();
    }

    @Test
    void givenNoAccountsPresent_whenFindAll_theEmptyListReturned() {
        when(accountRepository.findAll(Pageable.ofSize(1).withPage(0))).thenReturn(Page.empty());

        final PageResponse<AccountResponse> result = underTest.findAll(1, 0);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPage()).isZero();
        assertThat(result.getSize()).isZero();
        assertThat(result.getTotalPages()).isOne();
        assertThat(result.getTotalSize()).isZero();
        verify(accountRepository).findAll(Pageable.ofSize(1).withPage(0));
    }

    @Test
    void givenAccountPresent_whenFindAll_thenAccountsReturned() {
        when(accountRepository.findAll(Pageable.ofSize(2).withPage(1))).thenReturn(page);
        final Account account1 = account("TestName1", "SortCode1", 1234567, "bid1");
        final Account account2 = account("TestName2", "SortCode1", 1234568, "bid2");
        when(page.getContent()).thenReturn(List.of(account1, account2));
        when(page.getNumber()).thenReturn(1);
        when(page.getTotalPages()).thenReturn(4);
        when(page.getTotalElements()).thenReturn(16l);

        final PageResponse<AccountResponse> result = underTest.findAll(2, 1);

        assertThat(result.getContent()).hasSameElementsAs(List.of(accountResponse(account1), accountResponse(account2)));
        assertThat(result.getPage()).isOne();
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(4);
        assertThat(result.getTotalSize()).isEqualTo(16l);
    }

    @Test
    void givenAccountNotPresentForBid_whenFindByBid_thenDataNotFoundException() {
        when(accountRepository.getAccountByBusinessId("bid")).thenReturn(Optional.empty());

        assertThatExceptionOfType(DataNotFoundException.class)
            .isThrownBy(() -> underTest.findByBid("bid"))
            .withMessage("Account not found with bid 'bid'");
    }

    @Test
    void givenAccountPresentForBid_whenFindByBid_thenAccountResponseReturned() {
        when(accountRepository.getAccountByBusinessId(bidArgumentCaptor.capture())).thenReturn(Optional.of(account("name", "sortCode", 123456789, "bid")));

        final AccountResponse response = underTest.findByBid("bid");

        assertThat(bidArgumentCaptor.getValue()).isEqualTo("bid");
        assertThat(response.getAccountId()).isEqualTo("bid");
        assertThat(response.getNumber()).isEqualTo(123456789);
        assertThat(response.getName()).isEqualTo("name");
    }

    @Test
    void givenAccountNotPresentForBid_whenGetByBid_thenEmptyOptionalReturned() {
        when(accountRepository.getAccountByBusinessId(bidArgumentCaptor.capture())).thenReturn(Optional.empty());

        assertThat(underTest.getByBusinessId("bid")).isEmpty();
    }

    @Test
    void givenAccountPresentForBid_whenGetByBid_thenAccountResponseReturned() {
        when(accountRepository.getAccountByBusinessId(bidArgumentCaptor.capture())).thenReturn(Optional.of(account("name", "sortCode", 123456789, "bid")));

        final Optional<Account> response = underTest.getByBusinessId("bid");

        assertThat(bidArgumentCaptor.getValue()).isEqualTo("bid");
        assertThat(response.get().getBusinessId()).isEqualTo("bid");
        assertThat(response.get().getNumber()).isEqualTo(123456789);
        assertThat(response.get().getName()).isEqualTo("name");
    }

    @Test
    void givenCustomerNotPresentForBid_whenCreate_thenDataNotFoundForCustomerBidThrown() {
        AccountRequest request = AccountRequest.builder().withCustomerBid("cBid").withName("Test").build();
        assertThatThrownBy(() -> underTest.create(request))
            .isInstanceOf(DataNotFoundException.class)
            .hasMessage("Customer not found for bid 'cBid'");

        verify(customerService).getByBusinessId("cBid");
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void givenCustomerPresentForBid_whenCreate_thenAccountCreated() {
        Customer customer = mock(Customer.class);
        when(customerService.getByBusinessId("cBid")).thenReturn(Optional.of(customer));
        Account account = account("name", "sortcode", 123456789, "bid");
        when(accountRepository.save(accountArgumentCaptor.capture())).thenReturn(account);

        AccountResponse response = underTest.create(AccountRequest.builder().withCustomerBid("cBid").withName("Test").build());

        assertThat(accountArgumentCaptor.getValue().getCustomer()).isEqualTo(customer);
        assertThat(accountArgumentCaptor.getValue().getName()).isEqualTo("Test");
        assertThat(response.getCustomerId()).isEqualTo("cbid");
        assertThat(response.getName()).isEqualTo("name");
    }

    @Test
    void givenAccountNotPresentForBid_whenDelete_thenDataNotFoundException() {
        when(accountRepository.getAccountByBusinessId("bid")).thenReturn(Optional.empty());

        assertThatExceptionOfType(DataNotFoundException.class)
            .isThrownBy(() -> underTest.delete("bid"))
            .withMessage("Account not found with bid 'bid'");

        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    void givenAccountPresentForBid_whenDelete_thenCustomerDeleted() {
        when(accountRepository.getAccountByBusinessId(bidArgumentCaptor.capture())).thenReturn(Optional.of(account("name", "sortCode", 123456789, "bid")));
        doNothing().when(accountRepository).delete(accountArgumentCaptor.capture());

        underTest.delete("bid");

        assertThat(bidArgumentCaptor.getValue()).isEqualTo("bid");
        assertThat(accountArgumentCaptor.getValue().getBusinessId()).isEqualTo("bid");
    }

    @Test
    void givenCustomerNotPresentForBid_getAccountsForACustomer_thenDataNotFoundException() {
        when(customerService.getByBusinessId(bidArgumentCaptor.capture())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getAccountsForACustomer("cbid", 0, 1))
            .isInstanceOf(DataNotFoundException.class)
            .hasMessage("Customer not found for bid 'cbid'");

        assertThat(bidArgumentCaptor.getValue()).isEqualTo("cbid");
        verify(accountRepository, never()).findAccountsByCustomer(any(Customer.class), any(Pageable.class));
    }

    @Test
    void givenCustomerPresentForBidWithoutAnyAccount_getAccountsForACustomer_thenEmptyAccounts() {
        Customer customer = mock(Customer.class);
        when(customerService.getByBusinessId(bidArgumentCaptor.capture())).thenReturn(Optional.of(customer));
        when(accountRepository.findAccountsByCustomer(customer, Pageable.ofSize(1).withPage(0))).thenReturn(Page.empty());
        PageResponse<AccountResponse> accounts = underTest.getAccountsForACustomer("cbid", 0, 1);

        assertThat(bidArgumentCaptor.getValue()).isEqualTo("cbid");
        assertThat(accounts.getContent()).isEmpty();
    }

    @Test
    void givenCustomerPresentForBidWithAccount_getAccountsForACustomer_thenPaginatedDataWithAccounts() {
        Customer customer = mock(Customer.class);
        when(customerService.getByBusinessId(bidArgumentCaptor.capture())).thenReturn(Optional.of(customer));
        final Account account1 = account("TestName1", "SortCode1", 1234567, "bid1");
        when(page.getContent()).thenReturn(List.of(account1));
        when(page.getNumber()).thenReturn(1);
        when(page.getTotalPages()).thenReturn(4);
        when(page.getTotalElements()).thenReturn(16l);
        when(accountRepository.findAccountsByCustomer(customer, Pageable.ofSize(1).withPage(0))).thenReturn(page);

        PageResponse<AccountResponse> accounts = underTest.getAccountsForACustomer("cbid", 0, 1);

        assertThat(bidArgumentCaptor.getValue()).isEqualTo("cbid");
        assertThat(accounts.getContent()).hasSize(1);
        assertThat(accounts.getContent().get(0).getAccountId()).isEqualTo("bid1");
        assertThat(accounts.getContent().get(0).getName()).isEqualTo("TestName1");
        assertThat(accounts.getContent().get(0).getSortCode()).isEqualTo("SortCode1");
        assertThat(accounts.getContent().get(0).getNumber()).isEqualTo(1234567);
    }
}