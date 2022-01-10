package com.accountservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class Account extends BaseEntity {
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String description;
    @Column(nullable = false)
    String sortCode;
    @Column(nullable = false)
    Integer number;
    @Column(nullable = false)
    String currency;

    @ManyToOne
    @JoinColumn(nullable = false, insertable = false, updatable = false, name = "customer_id")
    Customer customer;
}
