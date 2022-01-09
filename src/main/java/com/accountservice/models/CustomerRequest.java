package com.accountservice.models;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(setterPrefix = "with")
@ApiModel("Model to create a customer")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CustomerRequest implements Serializable {
    private static final long serialVersionUID = -9097736513172697589L;
    private static final String NAME_REGEX = "[aA-zZ]{5,50}";

    @ApiModelProperty(required = true, value = "First Name of the customer. Required unique in system", example = "Xyz")
    @NotBlank
    @Pattern(regexp = NAME_REGEX)
    String firstName;

    @ApiModelProperty(required = true, value = "Last Name of the customer. Required unique in system", example = "Xyz")
    @NotBlank
    @Pattern(regexp = NAME_REGEX)
    String lastName;

}
