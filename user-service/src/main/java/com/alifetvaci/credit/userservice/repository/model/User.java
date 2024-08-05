package com.alifetvaci.credit.userservice.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USER",uniqueConstraints = {
        @UniqueConstraint(name = "USER_IDENTIFICATION_NUMBER", columnNames = {"IDENTIFICATION_NUMBER"})
})
@Entity
public class User extends BaseEntity{

    @Column(name = "IDENTIFICATION_NUMBER", nullable = false, updatable = false)
    private String identificationNumber;

    @Column(name = "FIRSTNAME", nullable = false)
    private String firstname;

    @Column(name = "LASTNAME", nullable = false)
    private String lastname;

    @Column(name = "PASSWORD", nullable = false)
    private String password;
}
