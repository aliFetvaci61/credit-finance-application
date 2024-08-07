package com.alifetvaci.creditservice.repository.model;



import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public class BaseDocument {

    @Id
    private int id;

    private Long version;

    private boolean deleted;

    private long createdAt;

    private long updatedAt;
}
