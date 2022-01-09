package com.accountservice.exceptions;

public class DataNotFoundException extends RuntimeException {
    private final String field;

    public DataNotFoundException(final String field, final String message) {
        super(message);
        this.field = field;
    }

    public DataNotFoundException(final String field) {
        this(null, field);
    }

    public DataNotFoundException() {
        this(null, null);
    }

    public String getField() {
        return field;
    }
}