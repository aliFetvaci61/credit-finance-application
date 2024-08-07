package com.alifetvaci.creditservice.repository;

import com.alifetvaci.creditservice.repository.model.entity.Credit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditRepository extends CrudRepository<Credit, Integer> {
    Optional<Credit> findByIdAndIdentificationNumber(int id, String identificationNumber);
}
