package com.accountservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;

@Data
@Entity
public class Customer extends BaseEntity {
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;
}
