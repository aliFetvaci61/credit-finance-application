package com.alifetvaci.creditservice.repository;

import com.alifetvaci.creditservice.repository.model.Credit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditRepository extends CrudRepository<Credit, Integer> {

    List<Credit> findByIdentificationNumber(String identificationNumber);
}
