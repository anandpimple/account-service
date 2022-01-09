package com.accountservice.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(setterPrefix = "with")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel("Model for displaying  error message")
public class ErrorMessage {
    @ApiModelProperty(value = "The error message", example = "Data is not correct")
    String message;
    @ApiModelProperty(value = "Field responsible for this error", example = "Bid")
    String field;
    @ApiModelProperty(value = "Severity of error", example = "DATA")
    Severity severity;
    @ApiModelProperty(value = "Type of the error", example = "ConstraintViolationException")
    String type;
}