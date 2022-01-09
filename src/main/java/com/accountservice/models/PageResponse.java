package com.accountservice.models;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder(setterPrefix = "with")
@ApiModel("To get paginated result")
public class PageResponse<T extends Serializable> implements Iterable<T> {
    @ApiModelProperty(
        required = true,
        value = "List of responses on the current page"
    )
    private final List<T> content;
    @ApiModelProperty(
        required = true,
        value = "Page size"
    )
    private final int size;
    @ApiModelProperty(
        required = true,
        value = "Total count"
    )
    private final long totalSize;
    @ApiModelProperty(
        required = true,
        value = "Current page number. Its 0 based. i.e firstpage =0, secondpage=1 "
    )
    private final int page;
    @ApiModelProperty(
        required = true,
        value = "Total pages"
    )
    private final int totalPages;

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }
}
