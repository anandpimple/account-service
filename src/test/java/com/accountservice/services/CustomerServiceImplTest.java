package com.accountservice.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
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

import com.accountservice.daos.CustomerRepository;
import com.accountservice.entities.Customer;
import com.accountservice.exceptions.DataNotFoundException;
import com.accountservice.models.CustomerRequest;
import com.accountservice.models.CustomerResponse;
import com.accountservice.models.PageResponse;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private Page page;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> bidArgumentCaptor;

    @InjectMocks
    private CustomerServiceImpl underTest;

    private static Customer customer(final String fName,
                                     final String lName,
                                     final String bid) {
        Customer customer = new Customer();
        customer.setFirstName(fName);
        customer.setLastName(lName);
        customer.setBusinessId(bid);
        customer.setCreatedOn(Timestamp.valueOf(LocalDateTime.now()));
        return customer;
    }

    private static CustomerResponse customerResponse(final Customer customer) {
        return CustomerResponse
            .builder()
            .withCustomerId(customer.getBusinessId())
            .withFirstName(customer.getFirstName())
            .withLastName(customer.getLastName())
            .withCreatedOn(customer.getCreatedOn().toLocalDateTime())
            .build();
    }

    @Test
    void givenNoCustomersPresent_whenFindAll_theEmptyListReturned() {
        when(customerRepository.findAll(Pageable.ofSize(1).withPage(0))).thenReturn(Page.empty());

        final PageResponse<CustomerResponse> result = underTest.findAll(1, 0);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPage()).isZero();
        assertThat(result.getSize()).isZero();
        assertThat(result.getTotalPages()).isOne();
        assertThat(result.getTotalSize()).isZero();
        verify(customerRepository).findAll(Pageable.ofSize(1).withPage(0));
    }

    @Test
    void givenCustomersPresent_whenFindAll_thenCustomerReturned() {
        when(customerRepository.findAll(Pageable.ofSize(2).withPage(1))).thenReturn(page);
        final Customer customer1 = customer("TestName1", "TestLastName1", "TestBid1");
        final Customer customer2 = customer("TestName2", "TestLastName2", "TestBid2");
        when(page.getContent()).thenReturn(List.of(customer1, customer2));
        when(page.getNumber()).thenReturn(1);
        when(page.getTotalPages()).thenReturn(4);
        when(page.getTotalElements()).thenReturn(16l);

        final PageResponse<CustomerResponse> result = underTest.findAll(2, 1);

        assertThat(result.getContent()).hasSameElementsAs(List.of(customerResponse(customer1), customerResponse(customer2)));
        assertThat(result.getPage()).isOne();
        assertThat(result.getSize()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(4);
        assertThat(result.getTotalSize()).isEqualTo(16l);
    }

    @Test
    void givenCustomerNotPresentForBid_whenFindByBid_thenDataNotFoundException() {
        when(customerRepository.getCustomerByBusinessId("bid")).thenReturn(Optional.empty());

        assertThatExceptionOfType(DataNotFoundException.class)
            .isThrownBy(() -> underTest.findByBid("bid"))
            .withMessage("Customer not found with bid 'bid'");

        verify(customerRepository).getCustomerByBusinessId("bid");
    }

    @Test
    void givenCustomerPresentForBid_whenFindByBid_thenCustomerResponseReturned() {
        when(customerRepository.getCustomerByBusinessId(bidArgumentCaptor.capture())).thenReturn(Optional.of(customer("fName", "lName", "bid")));

        final CustomerResponse response = underTest.findByBid("bid");

        assertThat(bidArgumentCaptor.getValue()).isEqualTo("bid");
        assertThat(response.getFirstName()).isEqualTo("fName");
        assertThat(response.getLastName()).isEqualTo("lName");
        assertThat(response.getCustomerId()).isEqualTo("bid");
    }

    @Test
    void givenCustomerPresentForBid_whenGetByBusinessId_thenCustomerResponseReturned() {
        when(customerRepository.getCustomerByBusinessId(bidArgumentCaptor.capture())).thenReturn(Optional.of(customer("fName", "lName", "bid")));

        final Optional<Customer> response = underTest.getByBusinessId("bid");

        assertThat(bidArgumentCaptor.getValue()).isEqualTo("bid");
        assertThat(response.get().getFirstName()).isEqualTo("fName");
        assertThat(response.get().getLastName()).isEqualTo("lName");
        assertThat(response.get().getBusinessId()).isEqualTo("bid");
        assertThat(response.get().getCreatedOn()).isNotNull();
        assertThat(response.get().getDeletedOn()).isNull();
        assertThat(response.get().getModifiedOn()).isNull();
    }

    @Test
    void givenCustomerNotPresentForBid_whenGetByBusinessId_thenEmptyResponseReturned() {
        when(customerRepository.getCustomerByBusinessId(bidArgumentCaptor.capture())).thenReturn(Optional.empty());

        final Optional<Customer> response = underTest.getByBusinessId("bid");

        assertThat(bidArgumentCaptor.getValue()).isEqualTo("bid");
        assertThat(response).isEmpty();
    }

    @Test
    void whenCreate_thenCustomerAdded() {
        when(customerRepository.save(customerArgumentCaptor.capture())).thenReturn(customer("fName", "lName", "bid"));

        final CustomerResponse response = underTest.create(CustomerRequest.builder().withFirstName("fName").withLastName("lName").build());

        final Customer customerEntity = customerArgumentCaptor.getValue();
        assertThat(customerEntity.getFirstName()).isEqualTo("fName");
        assertThat(customerEntity.getLastName()).isEqualTo("lName");
        assertThat(response.getFirstName()).isEqualTo("fName");
        assertThat(response.getLastName()).isEqualTo("lName");
        assertThat(response.getCustomerId()).isEqualTo("bid");
    }

    @Test
    void givenCustomerNotPresentForBid_whenDelete_thenDataNotFoundException() {
        when(customerRepository.getCustomerByBusinessId("bid")).thenReturn(Optional.empty());

        assertThatExceptionOfType(DataNotFoundException.class)
            .isThrownBy(() -> underTest.delete("bid"))
            .withMessage("Customer not found with bid 'bid'");

        verify(customerRepository, never()).delete(any(Customer.class));
    }

    @Test
    void givenCustomerPresentForBid_whenDelete_thenCustomerDeleted() {
        when(customerRepository.getCustomerByBusinessId(bidArgumentCaptor.capture())).thenReturn(Optional.of(customer("fName", "lName", "bid")));

        underTest.delete("bid");

        assertThat(bidArgumentCaptor.getValue()).isEqualTo("bid");
    }

}