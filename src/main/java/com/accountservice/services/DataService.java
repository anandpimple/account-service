package com.accountservice.services;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.accountservice.entities.BaseEntity;
import com.accountservice.exceptions.DataNotFoundException;
import com.accountservice.models.PageResponse;

public interface DataService<RES extends Serializable, REQ extends Serializable, ENT extends BaseEntity> {
    default PageResponse<RES> findAll(final int size, final int pageNo) {
        getLogger().info("Retrieving {} for page no {} with max size {}", getName(), pageNo, size);
        return mapToPageResponse(getRepository().findAll(Pageable.ofSize(size).withPage(pageNo)));
    }

    default PageResponse<RES> mapToPageResponse(final Page<ENT> page) {
        return (PageResponse<RES>) PageResponse
            .builder()
            .withContent(page.getContent().stream().map(this::mapResponseFromEntity).collect(Collectors.toList()))
            .withPage(page.getNumber())
            .withTotalPages(page.getTotalPages())
            .withTotalSize(page.getTotalElements())
            .withSize(page.getContent().size())
            .build();
    }

    default RES create(final REQ request) {
        getLogger().info("Creating {} with data {}", getName(), request);
        return mapResponseFromEntity(getRepository().save(mapEntityFromRequest(request)));
    }

    default void delete(final String bId) {
        getLogger().info("Deleting {} with bId {}", getName(), bId);
        getRepository().delete(processGetByBid(bId));
    }

    default RES findByBid(final String bId) {
        getLogger().info("Finding {} with bId {}", getName(), bId);
        return mapResponseFromEntity(processGetByBid(bId));
    }

    private ENT processGetByBid(final String bId) {
        return getByBusinessId(bId).orElseThrow(() -> new DataNotFoundException("bid", String.format("%s not found with bid '%s'", getName(), bId)));
    }

    Logger getLogger();

    PagingAndSortingRepository<ENT, Long> getRepository();

    RES mapResponseFromEntity(final ENT entity);

    ENT mapEntityFromRequest(final REQ req);

    String getName();

    Optional<ENT> getByBusinessId(final String bId);
}
