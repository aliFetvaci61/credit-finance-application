package com.alifetvaci.creditservice.repository;

import com.alifetvaci.creditservice.repository.model.entity.Credit;
import com.alifetvaci.creditservice.repository.model.entity.Installment;
import com.alifetvaci.creditservice.repository.model.enums.InstallmentStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstallmentRepository extends CrudRepository<Installment, Integer> {

    List<Installment> findByCredit(Credit credit);
    List<Installment> findByCreditAndStatus(Credit credit, InstallmentStatus status);

}
