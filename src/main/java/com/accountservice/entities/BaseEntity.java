package com.accountservice.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.Where;

import lombok.Data;

@Data
@Where(clause = "deleted_on is null")
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true, name = "business_id")
    private String businessId;

    @Column(nullable = false, name = "created_on")
    private Timestamp createdOn;

    @Column(name = "modified_on")
    private Timestamp modifiedOn;

    @Column(name = "deleted_on")
    private Timestamp deletedOn;

    @PrePersist
    void setDefaultValues() {
        businessId = String.format("%s%s", this.getClass().getSimpleName().substring(0, 2).toUpperCase(), RandomStringUtils.randomNumeric(12, 12));
        createdOn = Timestamp.valueOf(LocalDateTime.now());
    }

    @PreUpdate
    void setModifiedDate() {
        modifiedOn = Timestamp.valueOf(LocalDateTime.now());
    }

    @PreRemove
    void setDeletedDate() {
        deletedOn = Timestamp.valueOf(LocalDateTime.now());
    }
}
