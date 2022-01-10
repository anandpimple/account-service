package com.accountservice.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class Customer extends BaseEntity {
    @Column(nullable = false)
    String firstName;

    @Column(nullable = false)
    String lastName;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "customer", fetch = FetchType.LAZY)
    List<Account> accounts;
}
