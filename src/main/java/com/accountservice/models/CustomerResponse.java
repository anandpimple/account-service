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
@ApiModel("Customer details")
public class CustomerResponse implements Serializable {
    private static final long serialVersionUID = 5871007476532166418L;

    @ApiModelProperty(required = true, value = "First name of the customer", example = "Xyz")
    String firstName;

    @ApiModelProperty(required = true, value = "Last name of the customer", example = "Xyz")
    String lastName;

    @ApiModelProperty(required = true, value = "Customer id", example = "CU12345678912")
    String customerId;

    @ApiModelProperty(required = true, value = "Customer creation date", example = "CU12345678912")
    LocalDateTime createdOn;

    @ApiModelProperty(required = true, value = "Customer modification date", example = "CU12345678912")
    LocalDateTime modifiedOn;

}
