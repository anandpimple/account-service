package com.accountservice.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(setterPrefix = "with")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel("Account details")
public class AccountResponse implements Serializable {
    @ApiModelProperty(required = true, value = "Account Name", example = "Xyz")
    String name;

    @ApiModelProperty(required = true, value = "Account Description", example = "Xyz")
    String description;

    @ApiModelProperty(required = true, value = "Account sortCode", example = "101010")
    String sortCode;

    @ApiModelProperty(required = true, value = "Account number", example = "1234567891")
    Integer number;

    @ApiModelProperty(required = true, value = "Account id", example = "AC12345678912")
    String accountId;

    @ApiModelProperty(required = true, value = "Customer id", example = "CU12345678912")
    String customerId;

    @ApiModelProperty(required = true, value = "Account currency", example = "GBP")
    String currency;

    @ApiModelProperty(required = true, value = "Account creation date")
    LocalDateTime createdOn;

    @ApiModelProperty(value = "Account modification date")
    LocalDateTime modifiedOn;

}
