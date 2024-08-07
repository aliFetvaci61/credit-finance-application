package com.alifetvaci.creditservice.repository;

import com.alifetvaci.creditservice.repository.model.CreditDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ElasticCreditRepository extends ElasticsearchRepository<CreditDocument, Integer> {

    Optional<CreditDocument> findByIdAndIdentificationNumber(int id, String identificationNumber);
    Optional<List<CreditDocument>> findByIdentificationNumber(String identificationNumber);


}
