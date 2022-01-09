package com.accountservice.controllers;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.accountservice.exceptions.DataNotFoundException;
import com.accountservice.models.ErrorMessage;
import com.accountservice.models.Severity;

// For reporting issues in controller request with proper json structure
@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {
    private static ResponseEntity<ErrorMessage> buildErrorResponseEntity(final String message,
                                                                         final String messageType,
                                                                         final String field,
                                                                         final Severity severity,
                                                                         final HttpStatus status) {
        return new ResponseEntity<>(
            ErrorMessage.builder()
                .withMessage(String.format("Issue while processing request : %s", message))
                .withSeverity(severity)
                .withType(messageType)
                .withField(field)
                .build(),
            new HttpHeaders(),
            status
        );
    }

    @ExceptionHandler({DataNotFoundException.class})
    public ResponseEntity<ErrorMessage> handleNotFoundException(final DataNotFoundException ex,
                                                                final WebRequest request) {
        return buildErrorResponseEntity(ex.getMessage(), ex.getClass().getSimpleName(), ex.getField(), Severity.DATA, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleConstraintViolationException(final ConstraintViolationException ex,
                                                                           final WebRequest request) {
        return buildErrorResponseEntity(ex.getMessage(), ex.getClass().getSimpleName(), null, Severity.DATA, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<ErrorMessage> handleRuntimeException(final RuntimeException ex,
                                                               final WebRequest request) {
        return buildErrorResponseEntity(ex.getMessage(), ex.getClass().getSimpleName(), null, Severity.FATAL, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
