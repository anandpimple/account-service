package com.accountservice.controllers;

public final class TestConstants {
    static final String CORRECT_BASIC_AUTH_DETAILS = "Basic YWNjb3VudC1zZXJ2aWNlOnBhc3N3MHJk";

    static final String INCORRECT_BASIC_AUTH_DETAILS = "Basic Y2FyLWFwaTpwYXNzdzByZA==";

    static final String EMPTY_BASIC_AUTH_DETAILS = " ";

    static final String SECURITY_HEADER = "Authorization";

    static final String CONTENT_XPATH = "content";

    static final String SIZE_XPATH = "size";

    static final String TOTAL_SIZE_XPATH = "totalSize";

    static final String TOTAL_PAGES_XPATH = "totalPages";

    static final String PAGE_XPATH = "page";

    static final String CONTENT_DATA_XPATH_FORMAT = "$." + CONTENT_XPATH + "[*].%s";

    static final String ERROR_MESSAGE_XPATH = "message";

    static final String ERROR_FIELD_XPATH = "field";

    static final String ERROR_SEVERITY_XPATH = "severity";

    static final String ERROR_TYPE_XPATH = "type";

    private TestConstants() {
    }
}
