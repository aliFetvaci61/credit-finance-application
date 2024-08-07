package com.alifetvaci.creditservice.repository.elastic;

import com.alifetvaci.creditservice.repository.elastic.document.InstallmentDocument;
import com.alifetvaci.creditservice.repository.model.enums.InstallmentStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ElasticInstallmentRepository extends ElasticsearchRepository<InstallmentDocument, Integer> {

    Optional<List<InstallmentDocument>> findByCreditId(int creditId);

    Optional<List<InstallmentDocument>> findByCreditIdAndStatus(int creditId, InstallmentStatus status);

    Optional<List<InstallmentDocument>> findInstallmentDocumentByDueDateBefore(long dueDate);

}
