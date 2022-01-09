package com.accountservice.controllers;

import static com.accountservice.controllers.TestConstants.CONTENT_DATA_XPATH_FORMAT;
import static com.accountservice.controllers.TestConstants.CONTENT_XPATH;
import static com.accountservice.controllers.TestConstants.CORRECT_BASIC_AUTH_DETAILS;
import static com.accountservice.controllers.TestConstants.EMPTY_BASIC_AUTH_DETAILS;
import static com.accountservice.controllers.TestConstants.ERROR_FIELD_XPATH;
import static com.accountservice.controllers.TestConstants.ERROR_MESSAGE_XPATH;
import static com.accountservice.controllers.TestConstants.ERROR_SEVERITY_XPATH;
import static com.accountservice.controllers.TestConstants.ERROR_TYPE_XPATH;
import static com.accountservice.controllers.TestConstants.INCORRECT_BASIC_AUTH_DETAILS;
import static com.accountservice.controllers.TestConstants.PAGE_XPATH;
import static com.accountservice.controllers.TestConstants.SECURITY_HEADER;
import static com.accountservice.controllers.TestConstants.SIZE_XPATH;
import static com.accountservice.controllers.TestConstants.TOTAL_PAGES_XPATH;
import static com.accountservice.controllers.TestConstants.TOTAL_SIZE_XPATH;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import com.accountservice.AccountServiceApp;
import com.accountservice.daos.CustomerRepository;
import com.accountservice.entities.Customer;
import com.accountservice.models.CustomerRequest;
import com.accountservice.models.ErrorMessage;
import com.accountservice.models.Severity;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = AccountServiceApp.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CustomerControllerITTest {
    private static final String CUSTOMERS_URI = "/customers";
    private static final String CUSTOMERS_BID_URI_FORMAT = CUSTOMERS_URI + "/%s";
    private static final String CUSTOMER_NOT_FOUND_FORMAT = "Issue while processing request : Customer not found with bid '%s'";
    private static final String INVALID_BID_MESSAGE_FOR_GET = "Issue while processing request : getCustomerByBid.bId: must match \"(CU)[0-9]{12}\"";
    private static final String INVALID_BID_MESSAGE_FOR_DELETE = "Issue while processing request : deleteCustomerByBid.bId: must match \"(CU)[0-9]{12}\"";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    private List<Customer> customersForDelete = new ArrayList<>();

    private static String notFoundErrorMessage(final String bid) {
        return format(CUSTOMER_NOT_FOUND_FORMAT, bid);
    }

    private static void checkErrorMessage(final ResultActions resultActions,
                                          final ResultMatcher statusResultMatchers,
                                          final ErrorMessage errorMessage) throws Exception {
        resultActions.andExpect(statusResultMatchers);

        if (errorMessage.getMessage() != null) {
            resultActions.andExpect(jsonPath(ERROR_MESSAGE_XPATH).value(errorMessage.getMessage()));
        }

        if (errorMessage.getSeverity() != null) {
            resultActions.andExpect(jsonPath(ERROR_SEVERITY_XPATH).value(errorMessage.getSeverity().toString()));
        }
        if (errorMessage.getField() != null) {
            resultActions.andExpect(jsonPath(ERROR_FIELD_XPATH).value(errorMessage.getField()));
        }

        if (errorMessage.getType() != null) {
            resultActions.andExpect(jsonPath(ERROR_TYPE_XPATH).value(errorMessage.getType()));
        }
    }

    @AfterEach
    void dataCleanUp() {
        if (!customersForDelete.isEmpty()) {
            customerRepository.deleteAll(customersForDelete);
            customersForDelete.clear();
        }
    }

    @Test
    void givenSecurityHeaderMissing_whenApiCall_thenForbiddenStatus() throws Exception {
        mockMvc.perform(get(CUSTOMERS_URI)).andExpect(status().isUnauthorized());
    }

    @Test
    void givenSecurityHeaderDataEmpty_whenApiCall_thenForbiddenStatus() throws Exception {
        mockMvc.perform(get(CUSTOMERS_URI).header(SECURITY_HEADER, EMPTY_BASIC_AUTH_DETAILS)).andExpect(status().isUnauthorized());
    }

    @Test
    void givenSecurityHeaderDataIncorrect_whenApiCall_thenForbiddenStatus() throws Exception {
        mockMvc.perform(get(CUSTOMERS_URI).header(SECURITY_HEADER, INCORRECT_BASIC_AUTH_DETAILS)).andExpect(status().isUnauthorized());
    }

    @Test
    void givenProperSecurityHeaderAndEmptyCustomersPresent_whenGetAllCustomersApi_thenOkStatusReturnedWithZeroCustomers() throws Exception {
        mockMvc.perform(get(CUSTOMERS_URI).header(SECURITY_HEADER, CORRECT_BASIC_AUTH_DETAILS))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(CONTENT_XPATH).isEmpty())
            .andExpect(jsonPath(SIZE_XPATH).value(0))
            .andExpect(jsonPath(TOTAL_SIZE_XPATH).value(0))
            .andExpect(jsonPath(PAGE_XPATH).value(0))
            .andExpect(jsonPath(TOTAL_PAGES_XPATH).value(0));
    }

    @Test
    void givenProperSecurityHeaderAndAllDeletedCustomersPresent_whenGetAllCustomersApi_thenOkStatusReturnedWithZeroCustomers() throws Exception {
        addCustomer("TestName1", "TestLastName1", true);
        addCustomer("TestName2", "TestLastName2", true);
        mockMvc.perform(get(CUSTOMERS_URI).header(SECURITY_HEADER, CORRECT_BASIC_AUTH_DETAILS))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(CONTENT_XPATH).isEmpty())
            .andExpect(jsonPath(SIZE_XPATH).value(0))
            .andExpect(jsonPath(TOTAL_SIZE_XPATH).value(0))
            .andExpect(jsonPath(PAGE_XPATH).value(0))
            .andExpect(jsonPath(TOTAL_PAGES_XPATH).value(0));
    }

    @Test
    void givenProperSecurityHeaderAndOneCustomersPresent_whenGetAllCustomersApi_thenOkStatusReturnedWithOneCustomers() throws Exception {
        addCustomer("TestName1", "TestLastName1", true);
        addCustomer("TestName2", "TestLastName2", false);
        mockMvc.perform(get(CUSTOMERS_URI).header(SECURITY_HEADER, CORRECT_BASIC_AUTH_DETAILS).param("size", "2"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(CONTENT_XPATH).isNotEmpty()).andExpect(jsonPath(format(CONTENT_DATA_XPATH_FORMAT, "firstName")).value("TestName2"))
            .andExpect(jsonPath(SIZE_XPATH).value(1))
            .andExpect(jsonPath(TOTAL_SIZE_XPATH).value(1))
            .andExpect(jsonPath(PAGE_XPATH).value(0))
            .andExpect(jsonPath(TOTAL_PAGES_XPATH).value(1));
    }

    @Test
    void givenProperSecurityHeaderAndOneCustomersPresentOnPageZero_whenGetAllCustomersApiWithPage1_thenOkStatusReturnedWithOneCustomers() throws Exception {
        addCustomer("TestName1", "TestLastName1", true);
        addCustomer("TestName2", "TestLastName2", false);
        mockMvc.perform(get(CUSTOMERS_URI).header(SECURITY_HEADER, CORRECT_BASIC_AUTH_DETAILS).param("pageNo", "1"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(CONTENT_XPATH).isEmpty())
            .andExpect(jsonPath(SIZE_XPATH).value(0))
            .andExpect(jsonPath(TOTAL_SIZE_XPATH).value(1))
            .andExpect(jsonPath(PAGE_XPATH).value(1))
            .andExpect(jsonPath(TOTAL_PAGES_XPATH).value(1));
    }

    @Test
    void givenProperSecurityHeaderAndDataAvailableOnSecondPageToo_whenGetAllCustomersApiWithPage1_thenOkStatusReturnedWithOneCustomers() throws Exception {
        addCustomer("TestName1", "TestLastName1", false);
        addCustomer("TestName2", "TestLastName2", false);
        mockMvc.perform(get(CUSTOMERS_URI).header(SECURITY_HEADER, CORRECT_BASIC_AUTH_DETAILS).param("pageNo", "1").param("size", "1"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(CONTENT_XPATH).isNotEmpty())
            .andExpect(jsonPath(format(CONTENT_DATA_XPATH_FORMAT, "firstName")).value("TestName2"))
            .andExpect(jsonPath(SIZE_XPATH).value(1))
            .andExpect(jsonPath(TOTAL_SIZE_XPATH).value(2))
            .andExpect(jsonPath(PAGE_XPATH).value(1))
            .andExpect(jsonPath(TOTAL_PAGES_XPATH).value(2));
    }

    @Test
    void givenInvalidBid_whenGetByBid_theBadRequestStatus() throws Exception {
        final ErrorMessage errorMessage = ErrorMessage.builder().withMessage(INVALID_BID_MESSAGE_FOR_GET).withSeverity(Severity.DATA).withType("ConstraintViolationException").build();
        testRequestIssueForGetByBid("AU123456789011", status().isBadRequest(), errorMessage);
        testRequestIssueForGetByBid("CU12345678901", status().isBadRequest(), errorMessage);
        testRequestIssueForGetByBid("CU12345678901X", status().isBadRequest(), errorMessage);
        testRequestIssueForGetByBid(" ", status().isBadRequest(), errorMessage);
    }

    @Test
    void givenBidNotPresent_whenGetByBid_theNotFoundStatus() throws Exception {
        testRequestIssueForGetByBid("CU123456789011", status().isNotFound(),
            ErrorMessage.builder().withField("bid").withSeverity(Severity.DATA).withMessage(notFoundErrorMessage("CU123456789011")).withType("DataNotFoundException").build());
    }

    @Test
    void givenBidPresentButInDeletedState_whenGetByBid_theNotFoundStatus() throws Exception {
        final String businessId = addCustomer("TestName", "TestLastName", true);
        testRequestIssueForGetByBid(businessId, status().isNotFound(),
            ErrorMessage.builder().withField("bid").withSeverity(Severity.DATA).withMessage(notFoundErrorMessage(businessId)).withType("DataNotFoundException").build());
    }

    @Test
    void givenBidPresent_whenGetByBid_theOkStatusWithCustomerResponse() throws Exception {
        final String businessId = addCustomer("TestName", "TestLastName", false);
        mockMvc.perform(get(format(CUSTOMERS_BID_URI_FORMAT, businessId)).header(SECURITY_HEADER, CORRECT_BASIC_AUTH_DETAILS).param("pageNo", "1").param("size", "1"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("firstName").value("TestName"));
    }

    @Test
    void givenInvalidBid_whenDeleteByBid_theBadRequestStatus() throws Exception {
        final ErrorMessage errorMessage = ErrorMessage.builder().withMessage(INVALID_BID_MESSAGE_FOR_DELETE).withSeverity(Severity.DATA).withType("ConstraintViolationException").build();
        testRequestIssueForDeleteByBid("AU123456789011", status().isBadRequest(), errorMessage);
        testRequestIssueForDeleteByBid("CU12345678901", status().isBadRequest(), errorMessage);
        testRequestIssueForDeleteByBid("CU12345678901X", status().isBadRequest(), errorMessage);
        testRequestIssueForDeleteByBid(" ", status().isBadRequest(), errorMessage);
    }

    @Test
    void givenBidNotPresent_whenDeleteByBid_theNotFoundStatus() throws Exception {
        testRequestIssueForDeleteByBid("CU123456789011", status().isNotFound(),
            ErrorMessage.builder().withField("bid").withSeverity(Severity.DATA).withMessage(notFoundErrorMessage("CU123456789011")).withType("DataNotFoundException").build());
    }

    @Test
    void givenBidPresentButInDeletedState_whenDeleteByBid_theNotFoundStatus() throws Exception {
        final String businessId = addCustomer("TestName", "TestLastName", true);
        testRequestIssueForDeleteByBid(businessId, status().isNotFound(),
            ErrorMessage.builder().withField("bid").withSeverity(Severity.DATA).withMessage(notFoundErrorMessage(businessId)).withType("DataNotFoundException").build());
    }

    @Test
    void givenBidPresent_whenDeleteByBid_theOkStatusWithCustomerResponse() throws Exception {
        final String businessId = addCustomer("TestName", "TestLastName", false);

        mockMvc.perform(delete(format(CUSTOMERS_BID_URI_FORMAT, businessId)).header(SECURITY_HEADER, CORRECT_BASIC_AUTH_DETAILS).param("pageNo", "1").param("size", "1"))
            .andExpect(status().isOk());
        customersForDelete.clear();
        assertThat(customerRepository.getCustomerByBusinessId(businessId)).isEmpty();
    }

    @Test
    void givenImproperCreateRequest_whenCreateCustomer_thenBadRequestStatus() throws Exception {
        testBadRequestWhenIncorrectCustomerData(null, "");
        testBadRequestWhenIncorrectCustomerData(CustomerRequest.builder().build(), "");
        testBadRequestWhenIncorrectCustomerData(CustomerRequest.builder().withFirstName("123456").withLastName("Lname").build(), "");
        testBadRequestWhenIncorrectCustomerData(CustomerRequest.builder().withFirstName("Fname").withLastName("123456").build(), "");
        testBadRequestWhenIncorrectCustomerData(CustomerRequest.builder().withFirstName("Fname").withLastName("LnameLnameLnameLnameLnameLnameLnameLnameLnameLnamen").build(), "");
        testBadRequestWhenIncorrectCustomerData(CustomerRequest.builder().withLastName("Lname").withFirstName("FnameFnameFnameFnameFnameFnameFnameFnameFnameFnamen").build(), "");
    }

    @Test
    void givenProperCreateRequest_whenCreateCustomer_thenCustomerCreated() throws Exception {
        final CustomerRequest customerRequest = CustomerRequest.builder().withFirstName("Fname").withLastName("LnameLnameLnameLnameLnameLnameLnameLnameLnameLname").build();
        ResultActions actions = mockMvc.perform(post(CUSTOMERS_URI).header(SECURITY_HEADER, CORRECT_BASIC_AUTH_DETAILS).contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(customerRequest)));

        actions.andExpect(jsonPath("firstName").value("Fname"))
            .andExpect(jsonPath("lastName").value("LnameLnameLnameLnameLnameLnameLnameLnameLnameLname"));

        customerRepository.deleteByBusinessId((String) new ObjectMapper().readValue(actions.andReturn().getResponse().getContentAsString(), Map.class).get("customerId"));
    }

    private void testBadRequestWhenIncorrectCustomerData(final CustomerRequest customerRequest,
                                                         final String errorMessage) throws Exception {
        ResultActions actions = mockMvc.perform(post(CUSTOMERS_URI).header(SECURITY_HEADER, CORRECT_BASIC_AUTH_DETAILS).contentType(MediaType.APPLICATION_JSON).content(null != customerRequest ? new ObjectMapper().writeValueAsString(customerRequest) : " "));
        checkErrorMessage(actions, status().isBadRequest(), ErrorMessage.builder().build());
    }

    private void testRequestIssueForGetByBid(final String bid,
                                             final ResultMatcher statusResultMatchers,
                                             final ErrorMessage errorMessage) throws Exception {
        ResultActions actions = mockMvc.perform(get(format(CUSTOMERS_BID_URI_FORMAT, bid)).header(SECURITY_HEADER, CORRECT_BASIC_AUTH_DETAILS).param("pageNo", "1").param("size", "1"));
        checkErrorMessage(actions, statusResultMatchers, errorMessage);
    }

    private void testRequestIssueForDeleteByBid(final String bid,
                                                final ResultMatcher statusResultMatchers,
                                                final ErrorMessage errorMessage) throws Exception {
        ResultActions actions = mockMvc.perform(delete(format(CUSTOMERS_BID_URI_FORMAT, bid)).header(SECURITY_HEADER, CORRECT_BASIC_AUTH_DETAILS).param("pageNo", "1").param("size", "1"));
        checkErrorMessage(actions, statusResultMatchers, errorMessage);

    }

    private String addCustomer(final String fName,
                               final String lName,
                               boolean deleted) {
        Customer customer = new Customer();
        customer.setFirstName(fName);
        customer.setLastName(lName);
        customer = customerRepository.save(customer);

        if (deleted) {
            customerRepository.delete(customer);
        } else {
            customersForDelete.add(customer);
        }

        return customer.getBusinessId();
    }
}