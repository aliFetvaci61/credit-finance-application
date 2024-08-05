package com.alifetvaci.creditservice.repository;

import com.alifetvaci.creditservice.service.model.CreditInstallment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ElasticCreditInstallmentRepository extends ElasticsearchRepository<CreditInstallment, Integer> {

    Optional<List<CreditInstallment>> findByIdentificationNumber(String identificationNumber);

    Optional<CreditInstallment> findByIdAndIdentificationNumber(int id,String identificationNumber);

}
