package com.alifetvaci.credit.userservice.repository;


import com.alifetvaci.credit.userservice.repository.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByIdentificationNumber(String identificationNumber);
}
