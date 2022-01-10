package com.accountservice.models;

import static com.accountservice.Constants.CUSTOMER_BID_REGEX;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Currency;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(setterPrefix = "with")
@ApiModel("Model to create a account")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AccountRequest implements Serializable {
    private static final String NAME_REGEX = "[a-z.-_]{10,50}";

    @ApiModelProperty(required = true, value = "Name of the account", example = "TestAccount")
    @NotBlank
    @Pattern(regexp = NAME_REGEX)
    String name;

    @ApiModelProperty(required = true, value = "Description for the account", example = "AXAXAXAXA")
    @NotBlank
    @Size(max = 500)
    String description;

    @ApiModelProperty(required = true, value = "Sort code of the account", example = "AXAXAXAXA")
    @NotBlank
    @Pattern(regexp = "[0-9]{6,8}")
    String sortCode;

    @ApiModelProperty(required = true, value = "Account number", example = "12345671212")
    @NotNull
    @Positive
    Integer number;

    @ApiModelProperty(required = true, value = "Currency of the account", example = "GBP")
    @Currency(value = {"GBP", "EUR"})
    @NotBlank
    String currency;

    @ApiModelProperty(required = true, value = "Customer associated with account", example = "CU123456789012")
    @Pattern(regexp = CUSTOMER_BID_REGEX)
    @NotBlank
    String customerBid;
}
